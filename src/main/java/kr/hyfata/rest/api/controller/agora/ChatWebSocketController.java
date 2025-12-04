package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.chat.ChatMessageDto;
import kr.hyfata.rest.api.dto.agora.chat.ReadStatusDto;
import kr.hyfata.rest.api.entity.User;
import kr.hyfata.rest.api.entity.agora.Chat;
import kr.hyfata.rest.api.entity.agora.Message;
import kr.hyfata.rest.api.entity.agora.MessageReadStatus;
import kr.hyfata.rest.api.entity.agora.AgoraUserProfile;
import kr.hyfata.rest.api.repository.UserRepository;
import kr.hyfata.rest.api.repository.agora.AgoraUserProfileRepository;
import kr.hyfata.rest.api.repository.agora.ChatParticipantRepository;
import kr.hyfata.rest.api.repository.agora.ChatRepository;
import kr.hyfata.rest.api.repository.agora.MessageRepository;
import kr.hyfata.rest.api.repository.agora.MessageReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final AgoraUserProfileRepository agoraUserProfileRepository;
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageRepository messageRepository;
    private final MessageReadStatusRepository messageReadStatusRepository;

    /**
     * 메시지 전송
     * 클라이언트 → /app/agora/chat/{chatId}/send
     * 서버 → /topic/agora/chat/{chatId}
     */
    @MessageMapping("/agora/chat/{chatId}/send")
    @SendTo("/topic/agora/chat/{chatId}")
    public ChatMessageDto sendMessage(
            @DestinationVariable Long chatId,
            @Payload ChatMessageDto messageDto,
            StompHeaderAccessor accessor
    ) {
        String userEmail = accessor.getUser().getName();
        log.info("Message received from {} in chat {}: {}", userEmail, chatId, messageDto.getContent());

        User sender = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        // Verify user is participant
        if (!chatParticipantRepository.existsByChat_IdAndUser_Id(chatId, sender.getId())) {
            throw new IllegalStateException("User is not a participant of this chat");
        }

        // Create and save message
        Message.MessageType messageType = Message.MessageType.TEXT;
        if (messageDto.getType() != null) {
            try {
                messageType = Message.MessageType.valueOf(messageDto.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                messageType = Message.MessageType.TEXT;
            }
        }

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(messageDto.getContent())
                .type(messageType)
                .build();

        Message savedMessage = messageRepository.save(message);

        // Auto-mark as read for sender
        MessageReadStatus readStatus = MessageReadStatus.builder()
                .message(savedMessage)
                .user(sender)
                .build();
        messageReadStatusRepository.save(readStatus);

        // Get sender profile for response
        AgoraUserProfile senderProfile = agoraUserProfileRepository.findById(sender.getId()).orElse(null);

        // Build response DTO
        return ChatMessageDto.builder()
                .messageId(savedMessage.getId())
                .chatId(chatId)
                .senderId(sender.getId())
                .senderAgoraId(senderProfile != null ? senderProfile.getAgoraId() : "")
                .senderName(senderProfile != null ? senderProfile.getDisplayName() : "")
                .senderProfileImage(senderProfile != null ? senderProfile.getProfileImage() : null)
                .content(savedMessage.getContent())
                .type(savedMessage.getType().toString())
                .eventType("MESSAGE")
                .createdAt(savedMessage.getCreatedAt())
                .build();
    }

    /**
     * 읽음 처리
     * 클라이언트 → /app/agora/chat/{chatId}/read
     * 서버 → /topic/agora/chat/{chatId}
     */
    @MessageMapping("/agora/chat/{chatId}/read")
    @SendTo("/topic/agora/chat/{chatId}")
    public ReadStatusDto markAsRead(
            @DestinationVariable Long chatId,
            @Payload ReadStatusDto readDto,
            StompHeaderAccessor accessor
    ) {
        String userEmail = accessor.getUser().getName();
        log.info("Read status received from {} in chat {}", userEmail, chatId);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify user is participant
        if (!chatParticipantRepository.existsByChat_IdAndUser_Id(chatId, user.getId())) {
            throw new IllegalStateException("User is not a participant of this chat");
        }

        Message message = messageRepository.findById(readDto.getMessageId())
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        // Create read status if not already exists
        if (!messageReadStatusRepository.existsByMessage_IdAndUser_Id(message.getId(), user.getId())) {
            MessageReadStatus readStatus = MessageReadStatus.builder()
                    .message(message)
                    .user(user)
                    .build();
            messageReadStatusRepository.save(readStatus);
        }

        AgoraUserProfile userProfile = agoraUserProfileRepository.findById(user.getId()).orElse(null);

        return ReadStatusDto.builder()
                .chatId(chatId)
                .messageId(message.getId())
                .userId(user.getId())
                .userAgoraId(userProfile != null ? userProfile.getAgoraId() : "")
                .eventType("READ")
                .build();
    }
}
