package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.team.NoticeResponse;
import kr.hyfata.rest.api.dto.agora.team.CreateNoticeRequest;
import kr.hyfata.rest.api.dto.agora.team.UpdateNoticeRequest;
import kr.hyfata.rest.api.service.agora.AgoraTeamNoticeService;
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
@RequestMapping("/api/agora/teams/{teamId}/notices")
@RequiredArgsConstructor
@Slf4j
public class AgoraTeamNoticeController {

    private final AgoraTeamNoticeService agoraTeamNoticeService;

    /**
     * 공지 목록 조회
     * GET /api/agora/teams/{teamId}/notices
     */
    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getNoticeList(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        String userEmail = authentication.getName();
        List<NoticeResponse> notices = agoraTeamNoticeService.getNoticeList(userEmail, teamId);
        return ResponseEntity.ok(notices);
    }

    /**
     * 공지 상세 조회
     * GET /api/agora/teams/{teamId}/notices/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getNoticeDetail(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        NoticeResponse notice = agoraTeamNoticeService.getNoticeDetail(userEmail, teamId, id);
        return ResponseEntity.ok(notice);
    }

    /**
     * 공지 작성
     * POST /api/agora/teams/{teamId}/notices
     */
    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(
            Authentication authentication,
            @PathVariable Long teamId,
            @Valid @RequestBody CreateNoticeRequest request
    ) {
        String userEmail = authentication.getName();
        NoticeResponse notice = agoraTeamNoticeService.createNotice(userEmail, teamId, request);
        return ResponseEntity.ok(notice);
    }

    /**
     * 공지 수정
     * PUT /api/agora/teams/{teamId}/notices/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponse> updateNotice(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoticeRequest request
    ) {
        String userEmail = authentication.getName();
        NoticeResponse notice = agoraTeamNoticeService.updateNotice(userEmail, teamId, id, request);
        return ResponseEntity.ok(notice);
    }

    /**
     * 공지 삭제
     * DELETE /api/agora/teams/{teamId}/notices/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotice(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        String message = agoraTeamNoticeService.deleteNotice(userEmail, teamId, id);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}
