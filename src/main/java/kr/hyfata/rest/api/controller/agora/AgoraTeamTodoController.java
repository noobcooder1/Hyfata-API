package kr.hyfata.rest.api.controller.agora;

import kr.hyfata.rest.api.dto.agora.team.TodoResponse;
import kr.hyfata.rest.api.dto.agora.team.CreateTodoRequest;
import kr.hyfata.rest.api.dto.agora.team.UpdateTodoRequest;
import kr.hyfata.rest.api.service.agora.AgoraTeamTodoService;
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
@RequestMapping("/api/agora/teams/{teamId}/todos")
@RequiredArgsConstructor
@Slf4j
public class AgoraTeamTodoController {

    private final AgoraTeamTodoService agoraTeamTodoService;

    /**
     * 할일 목록 조회
     * GET /api/agora/teams/{teamId}/todos
     */
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getTodoList(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        String userEmail = authentication.getName();
        List<TodoResponse> todos = agoraTeamTodoService.getTodoList(userEmail, teamId);
        return ResponseEntity.ok(todos);
    }

    /**
     * 할일 상세 조회
     * GET /api/agora/teams/{teamId}/todos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoDetail(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        TodoResponse todo = agoraTeamTodoService.getTodoDetail(userEmail, teamId, id);
        return ResponseEntity.ok(todo);
    }

    /**
     * 할일 생성
     * POST /api/agora/teams/{teamId}/todos
     */
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            Authentication authentication,
            @PathVariable Long teamId,
            @Valid @RequestBody CreateTodoRequest request
    ) {
        String userEmail = authentication.getName();
        TodoResponse todo = agoraTeamTodoService.createTodo(userEmail, teamId, request);
        return ResponseEntity.ok(todo);
    }

    /**
     * 할일 수정
     * PUT /api/agora/teams/{teamId}/todos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTodoRequest request
    ) {
        String userEmail = authentication.getName();
        TodoResponse todo = agoraTeamTodoService.updateTodo(userEmail, teamId, id, request);
        return ResponseEntity.ok(todo);
    }

    /**
     * 할일 완료 처리
     * PUT /api/agora/teams/{teamId}/todos/{id}/complete
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<TodoResponse> completeTodo(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        TodoResponse todo = agoraTeamTodoService.completeTodo(userEmail, teamId, id);
        return ResponseEntity.ok(todo);
    }

    /**
     * 할일 삭제
     * DELETE /api/agora/teams/{teamId}/todos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(
            Authentication authentication,
            @PathVariable Long teamId,
            @PathVariable Long id
    ) {
        String userEmail = authentication.getName();
        String message = agoraTeamTodoService.deleteTodo(userEmail, teamId, id);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}
