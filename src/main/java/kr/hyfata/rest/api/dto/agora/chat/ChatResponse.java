package kr.hyfata.rest.api.dto.agora.chat;

import kr.hyfata.rest.api.entity.agora.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {

    private Long chatId;

    private String type;

    private String name;

    private String profileImage;

    private Long participantCount;

    private Long messageCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<String> participantAgoraIds;

    public static ChatResponse from(Chat chat, Long messageCount, List<String> participantAgoraIds) {
        return ChatResponse.builder()
                .chatId(chat.getId())
                .type(chat.getType().toString())
                .name(chat.getName())
                .profileImage(chat.getProfileImage())
                .participantCount((long) chat.getParticipants().size())
                .messageCount(messageCount)
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .participantAgoraIds(participantAgoraIds)
                .build();
    }
}
