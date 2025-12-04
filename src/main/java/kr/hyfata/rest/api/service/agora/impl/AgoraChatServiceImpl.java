package kr.hyfata.rest.api.service.agora.impl;

import kr.hyfata.rest.api.dto.agora.chat.ChatResponse;
import kr.hyfata.rest.api.dto.agora.chat.ChatListResponse;
import kr.hyfata.rest.api.dto.agora.chat.CreateChatRequest;
import kr.hyfata.rest.api.dto.agora.chat.MessageDto;
import kr.hyfata.rest.api.dto.agora.chat.SendMessageRequest;
import kr.hyfata.rest.api.entity.User;
import kr.hyfata.rest.api.entity.agora.Chat;
import kr.hyfata.rest.api.entity.agora.ChatParticipant;
import kr.hyfata.rest.api.entity.agora.Message;
import kr.hyfata.rest.api.entity.agora.MessageReadStatus;
import kr.hyfata.rest.api.entity.agora.AgoraUserProfile;
import kr.hyfata.rest.api.repository.UserRepository;
import kr.hyfata.rest.api.repository.agora.AgoraUserProfileRepository;
import kr.hyfata.rest.api.repository.agora.ChatRepository;
import kr.hyfata.rest.api.repository.agora.ChatParticipantRepository;
import kr.hyfata.rest.api.repository.agora.MessageRepository;
import kr.hyfata.rest.api.repository.agora.MessageReadStatusRepository;
import kr.hyfata.rest.api.service.agora.AgoraChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AgoraChatServiceImpl implements AgoraChatService {

    private final UserRepository userRepository;
    private final AgoraUserProfileRepository agoraUserProfileRepository;
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageRepository messageRepository;
    private final MessageReadStatusRepository messageReadStatusRepository;

    @Override
    public List<ChatListResponse> getChatList(String userEmail) {
        User user = findUserByEmail(userEmail);
        List<Chat> chats = chatRepository.findChatsByUserId(user.getId());

        return chats.stream()
                .map(chat -> {
                    Long participantCount = chatParticipantRepository.countByChat_Id(chat.getId());
                    List<Message> messages = messageRepository.findByChat_IdOrderByIdDesc(chat.getId(), PageRequest.of(0, 1));
                    Message lastMessage = messages.isEmpty() ? null : messages.get(0);

                    AgoraUserProfile senderProfile = null;
                    if (lastMessage != null) {
                        senderProfile = agoraUserProfileRepository.findById(lastMessage.getSender().getId()).orElse(null);
                    }

                    ChatParticipant chatParticipant = chatParticipantRepository.findByChat_IdAndUser_Id(chat.getId(), user.getId())
                            .orElse(null);
                    Boolean isPinned = chatParticipant != null ? chatParticipant.getIsPinned() : false;
                    java.time.LocalDateTime pinnedAt = chatParticipant != null ? chatParticipant.getPinnedAt() : null;

                    return ChatListResponse.from(chat, lastMessage, participantCount, isPinned, pinnedAt, senderProfile);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatResponse createChat(String userEmail, CreateChatRequest request) {
        User fromUser = findUserByEmail(userEmail);

        AgoraUserProfile targetProfile = agoraUserProfileRepository.findByAgoraId(request.getTargetAgoraId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with agoraId: " + request.getTargetAgoraId()));

        User toUser = targetProfile.getUser();

        // Check if direct chat already exists between these users
        java.util.Optional<Chat> existingChat = chatRepository.findDirectChatBetweenUsers(fromUser.getId(), toUser.getId());
        if (existingChat.isPresent()) {
            Chat chat = existingChat.get();
            Long messageCount = messageRepository.countByChat_IdAndIsDeletedFalse(chat.getId());
            List<String> participantAgoraIds = getParticipantAgoraIds(chat);
            return ChatResponse.from(chat, messageCount, participantAgoraIds);
        }

        // Create new direct chat
        Chat chat = Chat.builder()
                .type(Chat.ChatType.DIRECT)
                .createdBy(fromUser)
                .readEnabled(true)
                .build();

        Chat savedChat = chatRepository.save(chat);

        // Add participants
        ChatParticipant participant1 = ChatParticipant.builder()
                .chat(savedChat)
                .user(fromUser)
                .role(ChatParticipant.Role.MEMBER)
                .build();

        ChatParticipant participant2 = ChatParticipant.builder()
                .chat(savedChat)
                .user(toUser)
                .role(ChatParticipant.Role.MEMBER)
                .build();

        chatParticipantRepository.save(participant1);
        chatParticipantRepository.save(participant2);

        List<String> participantAgoraIds = getParticipantAgoraIds(savedChat);
        return ChatResponse.from(savedChat, 0L, participantAgoraIds);
    }

    @Override
    public ChatResponse getChatDetail(String userEmail, Long chatId) {
        User user = findUserByEmail(userEmail);

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        // Verify user is participant
        if (!chatParticipantRepository.existsByChat_IdAndUser_Id(chatId, user.getId())) {
            throw new IllegalStateException("User is not a participant of this chat");
        }

        Long messageCount = messageRepository.countByChat_IdAndIsDeletedFalse(chatId);
        List<String> participantAgoraIds = getParticipantAgoraIds(chat);

        return ChatResponse.from(chat, messageCount, participantAgoraIds);
    }

    @Override
    public List<MessageDto> getMessages(String userEmail, Long chatId, Long cursor, int limit) {
        User user = findUserByEmail(userEmail);

        // Verify user is participant
        if (!chatParticipantRepository.existsByChat_IdAndUser_Id(chatId, user.getId())) {
            throw new IllegalStateException("User is not a participant of this chat");
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Message> messages;

        if (cursor == null) {
            messages = messageRepository.findByChat_IdOrderByIdDesc(chatId, pageable);
        } else {
            messages = messageRepository.findByChat_IdAndIdLessThanOrderByIdDesc(chatId, cursor, pageable);
        }

        return messages.stream()
                .map(message -> {
                    AgoraUserProfile senderProfile = agoraUserProfileRepository.findById(message.getSender().getId())
                            .orElse(null);
                    return MessageDto.from(message, senderProfile);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageDto sendMessage(String userEmail, Long chatId, SendMessageRequest request) {
        User sender = findUserByEmail(userEmail);

        // Verify user is participant
        if (!chatParticipantRepository.existsByChat_IdAndUser_Id(chatId, sender.getId())) {
            throw new IllegalStateException("User is not a participant of this chat");
        }

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Message.MessageType messageType = Message.MessageType.TEXT;
        if (request.getType() != null) {
            try {
                messageType = Message.MessageType.valueOf(request.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                messageType = Message.MessageType.TEXT;
            }
        }

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(request.getContent())
                .type(messageType)
                .build();

        Message savedMessage = messageRepository.save(message);

        // Auto-mark as read for sender
        MessageReadStatus readStatus = MessageReadStatus.builder()
                .message(savedMessage)
                .user(sender)
                .build();
        messageReadStatusRepository.save(readStatus);

        AgoraUserProfile senderProfile = agoraUserProfileRepository.findById(sender.getId()).orElse(null);
        return MessageDto.from(savedMessage, senderProfile);
    }

    @Override
    @Transactional
    public String deleteMessage(String userEmail, Long chatId, Long messageId) {
        User user = findUserByEmail(userEmail);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        // Verify user is message sender
        if (!message.getSender().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only delete your own messages");
        }

        // Soft delete
        messageRepository.softDeleteById(messageId);

        return "Message deleted";
    }

    @Override
    @Transactional
    public String markChatAsRead(String userEmail, Long chatId) {
        User user = findUserByEmail(userEmail);

        // Verify user is participant
        if (!chatParticipantRepository.existsByChat_IdAndUser_Id(chatId, user.getId())) {
            throw new IllegalStateException("User is not a participant of this chat");
        }

        // Get all messages in chat that haven't been read by user
        List<Message> messages = messageRepository.findByChat_IdOrderByIdDesc(chatId, PageRequest.of(0, Integer.MAX_VALUE));

        for (Message message : messages) {
            if (!messageReadStatusRepository.existsByMessage_IdAndUser_Id(message.getId(), user.getId())) {
                MessageReadStatus readStatus = MessageReadStatus.builder()
                        .message(message)
                        .user(user)
                        .build();
                messageReadStatusRepository.save(readStatus);
            }
        }

        return "Chat marked as read";
    }

    private User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private List<String> getParticipantAgoraIds(Chat chat) {
        return chat.getParticipants().stream()
                .map(participant -> {
                    AgoraUserProfile profile = agoraUserProfileRepository.findById(participant.getUser().getId())
                            .orElse(null);
                    return profile != null ? profile.getAgoraId() : "";
                })
                .collect(Collectors.toList());
    }
}
