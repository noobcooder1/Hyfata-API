package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.team.TeamProfileResponse;
import kr.hyfata.rest.api.dto.agora.team.CreateTeamProfileRequest;
import kr.hyfata.rest.api.service.agora.AgoraTeamProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agora/teams/{teamId}/profile")
@RequiredArgsConstructor
@Slf4j
public class AgoraTeamProfileController {

    private final AgoraTeamProfileService agoraTeamProfileService;

    /**
     * 내 팀 프로필 조회
     * GET /api/agora/teams/{teamId}/profile
     */
    @GetMapping
    public ResponseEntity<TeamProfileResponse> getMyTeamProfile(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        String userEmail = authentication.getName();
        TeamProfileResponse profile = agoraTeamProfileService.getMyTeamProfile(userEmail, teamId);
        return ResponseEntity.ok(profile);
    }

    /**
     * 팀 프로필 생성
     * POST /api/agora/teams/{teamId}/profile
     */
    @PostMapping
    public ResponseEntity<TeamProfileResponse> createTeamProfile(
            Authentication authentication,
            @PathVariable Long teamId,
            @Valid @RequestBody CreateTeamProfileRequest request
    ) {
        String userEmail = authentication.getName();
        TeamProfileResponse profile = agoraTeamProfileService.createTeamProfile(userEmail, teamId, request);
        return ResponseEntity.ok(profile);
    }

    /**
     * 팀 프로필 수정
     * PUT /api/agora/teams/{teamId}/profile
     */
    @PutMapping
    public ResponseEntity<TeamProfileResponse> updateTeamProfile(
            Authentication authentication,
            @PathVariable Long teamId,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String profileImage
    ) {
        String userEmail = authentication.getName();
        TeamProfileResponse profile = agoraTeamProfileService.updateTeamProfile(userEmail, teamId, displayName, profileImage);
        return ResponseEntity.ok(profile);
    }

    /**
     * 팀 프로필 이미지 변경
     * PUT /api/agora/teams/{teamId}/profile/image
     */
    @PutMapping("/image")
    public ResponseEntity<TeamProfileResponse> updateTeamProfileImage(
            Authentication authentication,
            @PathVariable Long teamId,
            @RequestParam String profileImage
    ) {
        String userEmail = authentication.getName();
        TeamProfileResponse profile = agoraTeamProfileService.updateTeamProfileImage(userEmail, teamId, profileImage);
        return ResponseEntity.ok(profile);
    }

    /**
     * 타 팀원 프로필 조회
     * GET /api/agora/teams/{teamId}/members/{userId}/profile
     */
    @GetMapping("/members/{userId}")
    public ResponseEntity<TeamProfileResponse> getTeamMemberProfile(
            @PathVariable Long teamId,
            @PathVariable Long userId
    ) {
        TeamProfileResponse profile = agoraTeamProfileService.getTeamMemberProfile(teamId, userId);
        return ResponseEntity.ok(profile);
    }
}
