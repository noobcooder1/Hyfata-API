package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.friend.FriendRequestResponse;
import kr.hyfata.rest.api.dto.agora.friend.FriendResponse;
import kr.hyfata.rest.api.dto.agora.friend.SendFriendRequestDto;
import kr.hyfata.rest.api.service.agora.AgoraFriendService;
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
@RequestMapping("/api/agora/friends")
@RequiredArgsConstructor
@Slf4j
public class AgoraFriendController {

    private final AgoraFriendService agoraFriendService;

    /**
     * 친구 목록 조회
     * GET /api/agora/friends
     */
    @GetMapping
    public ResponseEntity<List<FriendResponse>> getFriendList(Authentication authentication) {
        String userEmail = authentication.getName();
        List<FriendResponse> friends = agoraFriendService.getFriendList(userEmail);
        return ResponseEntity.ok(friends);
    }

    /**
     * 친구 요청 전송
     * POST /api/agora/friends/request
     */
    @PostMapping("/request")
    public ResponseEntity<FriendRequestResponse> sendFriendRequest(
            Authentication authentication,
            @Valid @RequestBody SendFriendRequestDto request
    ) {
        String userEmail = authentication.getName();
        FriendRequestResponse response = agoraFriendService.sendFriendRequest(userEmail, request.getAgoraId());
        return ResponseEntity.ok(response);
    }

    /**
     * 받은 친구 요청 목록 조회
     * GET /api/agora/friends/requests
     */
    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequestResponse>> getReceivedRequests(Authentication authentication) {
        String userEmail = authentication.getName();
        List<FriendRequestResponse> requests = agoraFriendService.getReceivedFriendRequests(userEmail);
        return ResponseEntity.ok(requests);
    }

    /**
     * 친구 요청 수락
     * POST /api/agora/friends/requests/{id}/accept
     */
    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<FriendResponse> acceptFriendRequest(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        FriendResponse response = agoraFriendService.acceptFriendRequest(userEmail, id);
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 요청 거절
     * DELETE /api/agora/friends/requests/{id}
     */
    @DeleteMapping("/requests/{id}")
    public ResponseEntity<?> rejectFriendRequest(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        String message = agoraFriendService.rejectFriendRequest(userEmail, id);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 삭제
     * DELETE /api/agora/friends/{friendId}
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> deleteFriend(
            Authentication authentication,
            @PathVariable Long friendId
    ) {
        String userEmail = authentication.getName();
        String message = agoraFriendService.deleteFriend(userEmail, friendId);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 즐겨찾기 추가
     * POST /api/agora/friends/{friendId}/favorite
     */
    @PostMapping("/{friendId}/favorite")
    public ResponseEntity<FriendResponse> addFavorite(
            Authentication authentication,
            @PathVariable Long friendId
    ) {
        String userEmail = authentication.getName();
        FriendResponse response = agoraFriendService.addFavorite(userEmail, friendId);
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 즐겨찾기 제거
     * DELETE /api/agora/friends/{friendId}/favorite
     */
    @DeleteMapping("/{friendId}/favorite")
    public ResponseEntity<FriendResponse> removeFavorite(
            Authentication authentication,
            @PathVariable Long friendId
    ) {
        String userEmail = authentication.getName();
        FriendResponse response = agoraFriendService.removeFavorite(userEmail, friendId);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 차단
     * POST /api/agora/friends/{friendId}/block
     */
    @PostMapping("/{friendId}/block")
    public ResponseEntity<?> blockUser(
            Authentication authentication,
            @PathVariable Long friendId
    ) {
        String userEmail = authentication.getName();
        String message = agoraFriendService.blockUser(userEmail, friendId);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 차단 해제
     * DELETE /api/agora/friends/{friendId}/block
     */
    @DeleteMapping("/{friendId}/block")
    public ResponseEntity<?> unblockUser(
            Authentication authentication,
            @PathVariable Long friendId
    ) {
        String userEmail = authentication.getName();
        String message = agoraFriendService.unblockUser(userEmail, friendId);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * 차단 목록 조회
     * GET /api/agora/friends/blocked
     */
    @GetMapping("/blocked")
    public ResponseEntity<List<FriendResponse>> getBlockedList(Authentication authentication) {
        String userEmail = authentication.getName();
        List<FriendResponse> blockedUsers = agoraFriendService.getBlockedUserList(userEmail);
        return ResponseEntity.ok(blockedUsers);
    }

    /**
     * 생일 목록 조회
     * GET /api/agora/friends/birthdays
     */
    @GetMapping("/birthdays")
    public ResponseEntity<List<FriendResponse>> getFriendBirthdays(Authentication authentication) {
        String userEmail = authentication.getName();
        List<FriendResponse> birthdays = agoraFriendService.getFriendBirthdayList(userEmail);
        return ResponseEntity.ok(birthdays);
    }
}
