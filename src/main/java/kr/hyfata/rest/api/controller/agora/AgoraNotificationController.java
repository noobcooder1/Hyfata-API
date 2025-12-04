package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.NotificationResponse;
import kr.hyfata.rest.api.dto.agora.RegisterFcmTokenRequest;
import kr.hyfata.rest.api.service.agora.AgoraNotificationService;
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
@RequestMapping("/api/agora/notifications")
@RequiredArgsConstructor
@Slf4j
public class AgoraNotificationController {

    private final AgoraNotificationService agoraNotificationService;

    /**
     * 알림 목록 조회
     * GET /api/agora/notifications
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication authentication) {
        String userEmail = authentication.getName();
        List<NotificationResponse> notifications = agoraNotificationService.getNotifications(userEmail);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 읽지 않은 알림 수 조회
     * GET /api/agora/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Authentication authentication) {
        String userEmail = authentication.getName();
        long unreadCount = agoraNotificationService.getUnreadCount(userEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        return ResponseEntity.ok(response);
    }

    /**
     * 알림 읽음 처리
     * PUT /api/agora/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        NotificationResponse notification = agoraNotificationService.markAsRead(userEmail, id);
        return ResponseEntity.ok(notification);
    }

    /**
     * 모든 알림 읽음 처리
     * PUT /api/agora/notifications/read-all
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        String userEmail = authentication.getName();
        String message = agoraNotificationService.markAllAsRead(userEmail);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * 알림 삭제
     * DELETE /api/agora/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        String message = agoraNotificationService.deleteNotification(userEmail, id);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * FCM 토큰 등록
     * POST /api/agora/notifications/fcm-token
     */
    @PostMapping("/fcm-token")
    public ResponseEntity<?> registerFcmToken(
            Authentication authentication,
            @Valid @RequestBody RegisterFcmTokenRequest request
    ) {
        String userEmail = authentication.getName();
        String message = agoraNotificationService.registerFcmToken(userEmail, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    /**
     * FCM 토큰 삭제
     * DELETE /api/agora/notifications/fcm-token?token=...
     */
    @DeleteMapping("/fcm-token")
    public ResponseEntity<?> unregisterFcmToken(
            Authentication authentication,
            @RequestParam String token
    ) {
        String userEmail = authentication.getName();
        String message = agoraNotificationService.unregisterFcmToken(userEmail, token);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}
