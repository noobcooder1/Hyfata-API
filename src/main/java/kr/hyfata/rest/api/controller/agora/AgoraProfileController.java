package kr.hyfata.rest.api.controller.agora;

import jakarta.validation.Valid;
import kr.hyfata.rest.api.dto.agora.AgoraProfileResponse;
import kr.hyfata.rest.api.dto.agora.CreateAgoraProfileRequest;
import kr.hyfata.rest.api.dto.agora.PublicAgoraProfileResponse;
import kr.hyfata.rest.api.dto.agora.UpdateAgoraProfileRequest;
import kr.hyfata.rest.api.service.agora.AgoraProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agora/profile")
@RequiredArgsConstructor
@Slf4j
public class AgoraProfileController {

    private final AgoraProfileService agoraProfileService;

    /**
     * 내 Agora 프로필 조회
     * GET /api/agora/profile
     */
    @GetMapping
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        AgoraProfileResponse profile = agoraProfileService.getMyProfile(email);

        if (profile == null) {
            return ResponseEntity.ok(Map.of(
                    "message", "Agora profile not found. Please create a profile first.",
                    "hasProfile", false
            ));
        }

        return ResponseEntity.ok(profile);
    }

    /**
     * Agora 프로필 생성
     * POST /api/agora/profile
     */
    @PostMapping
    public ResponseEntity<AgoraProfileResponse> createProfile(
            Authentication authentication,
            @Valid @RequestBody CreateAgoraProfileRequest request
    ) {
        String email = authentication.getName();
        AgoraProfileResponse profile = agoraProfileService.createProfile(email, request);
        return ResponseEntity.ok(profile);
    }

    /**
     * Agora 프로필 수정
     * PUT /api/agora/profile
     */
    @PutMapping
    public ResponseEntity<AgoraProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateAgoraProfileRequest request
    ) {
        String email = authentication.getName();
        AgoraProfileResponse profile = agoraProfileService.updateProfile(email, request);
        return ResponseEntity.ok(profile);
    }

    /**
     * 프로필 이미지 변경
     * PUT /api/agora/profile/image
     */
    @PutMapping("/image")
    public ResponseEntity<AgoraProfileResponse> updateProfileImage(
            Authentication authentication,
            @RequestBody Map<String, String> request
    ) {
        String email = authentication.getName();
        String imageUrl = request.get("profileImage");
        AgoraProfileResponse profile = agoraProfileService.updateProfileImage(email, imageUrl);
        return ResponseEntity.ok(profile);
    }

    /**
     * 타 사용자 프로필 조회
     * GET /api/agora/profile/{agoraId}
     */
    @GetMapping("/{agoraId}")
    public ResponseEntity<PublicAgoraProfileResponse> getUserProfile(
            @PathVariable String agoraId
    ) {
        PublicAgoraProfileResponse profile = agoraProfileService.getUserProfile(agoraId);
        return ResponseEntity.ok(profile);
    }

    /**
     * 사용자 검색
     * GET /api/agora/profile/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<Page<PublicAgoraProfileResponse>> searchUsers(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<PublicAgoraProfileResponse> results = agoraProfileService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * agoraId 중복 확인
     * GET /api/agora/profile/check-id?agoraId=xxx
     */
    @GetMapping("/check-id")
    public ResponseEntity<?> checkAgoraIdExists(
            @RequestParam String agoraId
    ) {
        boolean exists = agoraProfileService.checkAgoraIdExists(agoraId);
        Map<String, Object> response = new HashMap<>();
        response.put("agoraId", agoraId);
        response.put("exists", exists);
        response.put("available", !exists);
        return ResponseEntity.ok(response);
    }
}
