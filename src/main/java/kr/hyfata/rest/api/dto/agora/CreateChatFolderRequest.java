package kr.hyfata.rest.api.dto.agora;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatFolderRequest {

    @NotBlank(message = "name is required")
    private String name;

    private Integer orderIndex;
}
