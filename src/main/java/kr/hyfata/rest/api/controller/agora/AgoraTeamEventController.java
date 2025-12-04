package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.team.EventResponse;
import kr.hyfata.rest.api.dto.agora.team.CreateEventRequest;
import kr.hyfata.rest.api.dto.agora.team.UpdateEventRequest;
import kr.hyfata.rest.api.service.agora.AgoraTeamEventService;
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
@RequestMapping("/api/agora/teams/{teamId}/events")
@RequiredArgsConstructor
@Slf4j
public class AgoraTeamEventController {

    private final AgoraTeamEventService agoraTeamEventService;

    /**
     * 일정 목록 조회
     * GET /api/agora/teams/{teamId}/events
     */
    @GetMapping
    public ResponseEntity<List<EventResponse>> getEventList(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        String userEmail = authentication.getName();
        List<EventResponse> events = agoraTeamEventService.getEventList(userEmail, teamId);
        return ResponseEntity.ok(events);
    }

    /**
     * 일정 상세 조회
     * GET /api/agora/teams/{teamId}/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventDetail(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        EventResponse event = agoraTeamEventService.getEventDetail(userEmail, teamId, id);
        return ResponseEntity.ok(event);
    }

    /**
     * 일정 생성
     * POST /api/agora/teams/{teamId}/events
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            Authentication authentication,
            @PathVariable Long teamId,
            @Valid @RequestBody CreateEventRequest request
    ) {
        String userEmail = authentication.getName();
        EventResponse event = agoraTeamEventService.createEvent(userEmail, teamId, request);
        return ResponseEntity.ok(event);
    }

    /**
     * 일정 수정
     * PUT /api/agora/teams/{teamId}/events/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        String userEmail = authentication.getName();
        EventResponse event = agoraTeamEventService.updateEvent(userEmail, teamId, id, request);
        return ResponseEntity.ok(event);
    }

    /**
     * 일정 삭제
     * DELETE /api/agora/teams/{teamId}/events/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        String message = agoraTeamEventService.deleteEvent(userEmail, teamId, id);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}
