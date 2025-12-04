package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.ChatFolderResponse;
import kr.hyfata.rest.api.dto.agora.CreateChatFolderRequest;
import kr.hyfata.rest.api.service.agora.AgoraChatFolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agora/chats/folders")
@RequiredArgsConstructor
@Slf4j
public class AgoraChatFolderController {

    private final AgoraChatFolderService agoraChatFolderService;

    /**
     * 폴더 목록 조회
     * GET /api/agora/chats/folders
     */
    @GetMapping
    public ResponseEntity<List<ChatFolderResponse>> getChatFolders(Authentication authentication) {
        String userEmail = authentication.getName();
        List<ChatFolderResponse> folders = agoraChatFolderService.getChatFolders(userEmail);
        return ResponseEntity.ok(folders);
    }

    /**
     * 폴더 생성
     * POST /api/agora/chats/folders
     */
    @PostMapping
    public ResponseEntity<ChatFolderResponse> createChatFolder(
            Authentication authentication,
            @Valid @RequestBody CreateChatFolderRequest request
    ) {
        String userEmail = authentication.getName();
        ChatFolderResponse folder = agoraChatFolderService.createChatFolder(userEmail, request);
        return ResponseEntity.ok(folder);
    }

    /**
     * 폴더 수정
     * PUT /api/agora/chats/folders/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChatFolderResponse> updateChatFolder(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color
    ) {
        String userEmail = authentication.getName();
        ChatFolderResponse folder = agoraChatFolderService.updateChatFolder(userEmail, id, name, color);
        return ResponseEntity.ok(folder);
    }

    /**
     * 폴더 삭제
     * DELETE /api/agora/chats/folders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChatFolder(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        String message = agoraChatFolderService.deleteChatFolder(userEmail, id);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방을 폴더에 추가
     * POST /api/agora/chats/{chatId}/folder/{folderId}
     */
    @PostMapping("/{folderId}/chats/{chatId}")
    public ResponseEntity<ChatFolderResponse> addChatToFolder(
            Authentication authentication,
            @PathVariable Long chatId,
            @PathVariable Long folderId
    ) {
        String userEmail = authentication.getName();
        ChatFolderResponse folder = agoraChatFolderService.addChatToFolder(userEmail, chatId, folderId);
        return ResponseEntity.ok(folder);
    }

    /**
     * 채팅방을 폴더에서 제거
     * DELETE /api/agora/chats/{chatId}/folder
     */
    @DeleteMapping("/{folderId}/chats/{chatId}")
    public ResponseEntity<?> removeChatFromFolder(
            Authentication authentication,
            @PathVariable Long chatId
    ) {
        String userEmail = authentication.getName();
        String message = agoraChatFolderService.removeChatFromFolder(userEmail, chatId);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}
