package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.chat.GroupChatResponse;
import kr.hyfata.rest.api.dto.agora.chat.CreateGroupChatRequest;
import kr.hyfata.rest.api.dto.agora.chat.InviteMembersRequest;
import kr.hyfata.rest.api.service.agora.AgoraGroupChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agora/chats/groups")
@RequiredArgsConstructor
@Slf4j
public class AgoraGroupChatController {

    private final AgoraGroupChatService agoraGroupChatService;

    /**
     * 그룹 생성
     * POST /api/agora/chats/groups
     */
    @PostMapping
    public ResponseEntity<GroupChatResponse> createGroupChat(
            Authentication authentication,
            @Valid @RequestBody CreateGroupChatRequest request
    ) {
        String userEmail = authentication.getName();
        GroupChatResponse response = agoraGroupChatService.createGroupChat(userEmail, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 그룹 정보 조회
     * GET /api/agora/chats/groups/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroupChatResponse> getGroupChatDetail(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        GroupChatResponse response = agoraGroupChatService.getGroupChatDetail(userEmail, id);
        return ResponseEntity.ok(response);
    }

    /**
     * 그룹 정보 수정
     * PUT /api/agora/chats/groups/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<GroupChatResponse> updateGroupChat(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String profileImage
    ) {
        String userEmail = authentication.getName();
        GroupChatResponse response = agoraGroupChatService.updateGroupChat(userEmail, id, name, profileImage);
        return ResponseEntity.ok(response);
    }

    /**
     * 멤버 초대
     * POST /api/agora/chats/groups/{id}/members
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<GroupChatResponse> inviteMembers(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody InviteMembersRequest request
    ) {
        String userEmail = authentication.getName();
        GroupChatResponse response = agoraGroupChatService.inviteMembers(userEmail, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 멤버 제거
     * DELETE /api/agora/chats/groups/{id}/members/{userId}
     */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<?> removeMember(
            Authentication authentication,
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        String userEmail = authentication.getName();
        String message = agoraGroupChatService.removeMember(userEmail, id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * 그룹 나가기
     * DELETE /api/agora/chats/groups/{id}/leave
     */
    @DeleteMapping("/{id}/leave")
    public ResponseEntity<?> leaveGroup(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        String message = agoraGroupChatService.leaveGroup(userEmail, id);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}
