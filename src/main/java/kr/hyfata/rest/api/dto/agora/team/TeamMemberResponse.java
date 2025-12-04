package kr.hyfata.rest.api.dto.agora.team;

import kr.hyfata.rest.api.entity.agora.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberResponse {

    private Long memberId;

    private Long userId;

    private String userEmail;

    private String roleName;

    private LocalDateTime joinedAt;

    public static TeamMemberResponse from(TeamMember member) {
        return TeamMemberResponse.builder()
                .memberId(member.getId())
                .userId(member.getUser().getId())
                .userEmail(member.getUser().getEmail())
                .roleName(member.getRole() != null ? member.getRole().getName() : "member")
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
