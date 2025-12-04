package kr.hyfata.rest.api.dto.agora;

import kr.hyfata.rest.api.entity.agora.ChatFolder;
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
public class ChatFolderResponse {

    private Long folderId;

    private String name;

    private Integer orderIndex;

    private Long chatCount;

    private List<Long> chatIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static ChatFolderResponse from(ChatFolder folder, List<Long> chatIds) {
        return ChatFolderResponse.builder()
                .folderId(folder.getId())
                .name(folder.getName())
                .orderIndex(folder.getOrderIndex())
                .chatCount((long) folder.getItems().size())
                .chatIds(chatIds)
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .build();
    }
}
