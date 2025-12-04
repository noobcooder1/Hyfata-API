package kr.hyfata.rest.api.security;

import kr.hyfata.rest.api.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // WebSocket 연결 요청 시에만 JWT 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = null;
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            if (token == null) {
                log.warn("No JWT token provided for WebSocket connection");
                return message;
            }

            // JWT 토큰 검증
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                log.info("WebSocket connection authenticated for user: {}", email);

                // 검증된 사용자 정보를 Authentication에 설정
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        new ArrayList<>()
                );
                accessor.setUser(authentication);
            } else {
                log.warn("Invalid JWT token provided for WebSocket connection");
                return message;
            }
        }

        return message;
    }
}
