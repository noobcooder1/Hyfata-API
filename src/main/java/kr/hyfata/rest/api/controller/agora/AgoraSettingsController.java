package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.settings.NotificationSettingsResponse;
import kr.hyfata.rest.api.dto.agora.settings.PrivacySettingsResponse;
import kr.hyfata.rest.api.dto.agora.settings.UpdateNotificationSettingsRequest;
import kr.hyfata.rest.api.dto.agora.settings.UpdatePrivacySettingsRequest;
import kr.hyfata.rest.api.dto.agora.settings.UpdateBirthdayReminderRequest;
import kr.hyfata.rest.api.service.agora.AgoraSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agora/settings")
@RequiredArgsConstructor
@Slf4j
public class AgoraSettingsController {

    private final AgoraSettingsService agoraSettingsService;

    /**
     * 알림 설정 조회
     * GET /api/agora/settings/notifications
     */
    @GetMapping("/notifications")
    public ResponseEntity<NotificationSettingsResponse> getNotificationSettings(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        NotificationSettingsResponse settings = agoraSettingsService.getNotificationSettings(userEmail);
        return ResponseEntity.ok(settings);
    }

    /**
     * 알림 설정 수정
     * PUT /api/agora/settings/notifications
     */
    @PutMapping("/notifications")
    public ResponseEntity<NotificationSettingsResponse> updateNotificationSettings(
            Authentication authentication,
            @Valid @RequestBody UpdateNotificationSettingsRequest request
    ) {
        String userEmail = authentication.getName();
        NotificationSettingsResponse settings = agoraSettingsService.updateNotificationSettings(userEmail, request);
        return ResponseEntity.ok(settings);
    }

    /**
     * 개인정보 설정 조회
     * GET /api/agora/settings/privacy
     */
    @GetMapping("/privacy")
    public ResponseEntity<PrivacySettingsResponse> getPrivacySettings(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        PrivacySettingsResponse settings = agoraSettingsService.getPrivacySettings(userEmail);
        return ResponseEntity.ok(settings);
    }

    /**
     * 개인정보 설정 수정
     * PUT /api/agora/settings/privacy
     */
    @PutMapping("/privacy")
    public ResponseEntity<PrivacySettingsResponse> updatePrivacySettings(
            Authentication authentication,
            @Valid @RequestBody UpdatePrivacySettingsRequest request
    ) {
        String userEmail = authentication.getName();
        PrivacySettingsResponse settings = agoraSettingsService.updatePrivacySettings(userEmail, request);
        return ResponseEntity.ok(settings);
    }

    /**
     * 생일 알림 설정 수정
     * PUT /api/agora/settings/birthday-reminder
     */
    @PutMapping("/birthday-reminder")
    public ResponseEntity<NotificationSettingsResponse> updateBirthdayReminder(
            Authentication authentication,
            @Valid @RequestBody UpdateBirthdayReminderRequest request
    ) {
        String userEmail = authentication.getName();
        NotificationSettingsResponse settings = agoraSettingsService.updateBirthdayReminder(userEmail, request);
        return ResponseEntity.ok(settings);
    }
}
