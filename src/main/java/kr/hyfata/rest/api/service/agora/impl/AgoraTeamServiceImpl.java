package kr.hyfata.rest.api.service.agora.impl;

import kr.hyfata.rest.api.dto.agora.team.TeamResponse;
import kr.hyfata.rest.api.dto.agora.team.CreateTeamRequest;
import kr.hyfata.rest.api.dto.agora.team.TeamMemberResponse;
import kr.hyfata.rest.api.entity.User;
import kr.hyfata.rest.api.entity.agora.Team;
import kr.hyfata.rest.api.entity.agora.TeamMember;
import kr.hyfata.rest.api.entity.agora.TeamRole;
import kr.hyfata.rest.api.repository.UserRepository;
import kr.hyfata.rest.api.repository.agora.TeamRepository;
import kr.hyfata.rest.api.repository.agora.TeamMemberRepository;
import kr.hyfata.rest.api.repository.agora.TeamRoleRepository;
import kr.hyfata.rest.api.service.agora.AgoraTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AgoraTeamServiceImpl implements AgoraTeamService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRoleRepository teamRoleRepository;

    @Override
    public List<TeamResponse> getTeamList(String userEmail) {
        User user = findUserByEmail(userEmail);
        List<Team> teams = teamRepository.findTeamsByUserId(user.getId());
        return teams.stream()
                .map(TeamResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamResponse createTeam(String userEmail, CreateTeamRequest request) {
        User creator = findUserByEmail(userEmail);

        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .profileImage(request.getProfileImage())
                .createdBy(creator)
                .isMain(false)
                .build();

        Team savedTeam = teamRepository.save(team);

        // Create default ADMIN role for creator
        TeamRole adminRole = TeamRole.builder()
                .team(savedTeam)
                .name("admin")
                .permissions("all")
                .build();
        TeamRole savedRole = teamRoleRepository.save(adminRole);

        // Add creator as member with ADMIN role
        TeamMember member = TeamMember.builder()
                .team(savedTeam)
                .user(creator)
                .role(savedRole)
                .build();
        teamMemberRepository.save(member);

        return TeamResponse.from(savedTeam);
    }

    @Override
    public TeamResponse getTeamDetail(String userEmail, Long teamId) {
        User user = findUserByEmail(userEmail);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        // Verify user is member
        if (!teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, user.getId())) {
            throw new IllegalStateException("You are not a member of this team");
        }

        return TeamResponse.from(team);
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(String userEmail, Long teamId, String name, String description, String profileImage) {
        User user = findUserByEmail(userEmail);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        // Verify user is creator
        if (!team.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("Only team creator can update team");
        }

        if (name != null && !name.isEmpty()) {
            team.setName(name);
        }
        if (description != null) {
            team.setDescription(description);
        }
        if (profileImage != null) {
            team.setProfileImage(profileImage);
        }

        Team updated = teamRepository.save(team);
        return TeamResponse.from(updated);
    }

    @Override
    @Transactional
    public String deleteTeam(String userEmail, Long teamId) {
        User user = findUserByEmail(userEmail);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        // Verify user is creator
        if (!team.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("Only team creator can delete team");
        }

        teamRepository.deleteById(teamId);
        return "Team deleted";
    }

    @Override
    public List<TeamMemberResponse> getTeamMembers(String userEmail, Long teamId) {
        User user = findUserByEmail(userEmail);

        // Verify user is member
        if (!teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, user.getId())) {
            throw new IllegalStateException("You are not a member of this team");
        }

        List<TeamMember> members = teamMemberRepository.findByTeam_IdOrderByJoinedAtAsc(teamId);
        return members.stream()
                .map(TeamMemberResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamMemberResponse inviteMember(String userEmail, Long teamId, String targetUserEmail) {
        User inviter = findUserByEmail(userEmail);
        User targetUser = findUserByEmail(targetUserEmail);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        // Verify inviter is creator
        if (!team.getCreatedBy().getId().equals(inviter.getId())) {
            throw new IllegalStateException("Only team creator can invite members");
        }

        // Check if already member
        if (teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, targetUser.getId())) {
            throw new IllegalStateException("User is already a member of this team");
        }

        // Create default MEMBER role if not exists
        TeamRole memberRole = teamRoleRepository.findByTeam_IdAndName(teamId, "member")
                .orElseGet(() -> {
                    TeamRole role = TeamRole.builder()
                            .team(team)
                            .name("member")
                            .permissions("read,chat")
                            .build();
                    return teamRoleRepository.save(role);
                });

        TeamMember newMember = TeamMember.builder()
                .team(team)
                .user(targetUser)
                .role(memberRole)
                .build();

        TeamMember saved = teamMemberRepository.save(newMember);
        return TeamMemberResponse.from(saved);
    }

    @Override
    @Transactional
    public String removeMember(String userEmail, Long teamId, Long memberId) {
        User user = findUserByEmail(userEmail);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        // Verify user is creator
        if (!team.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("Only team creator can remove members");
        }

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Team member not found"));

        // Cannot remove creator
        if (member.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Creator cannot be removed");
        }

        teamMemberRepository.deleteById(memberId);
        return "Member removed";
    }

    @Override
    @Transactional
    public String changeMemberRole(String userEmail, Long teamId, Long memberId, String roleName) {
        User user = findUserByEmail(userEmail);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        // Verify user is creator
        if (!team.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("Only team creator can change member roles");
        }

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Team member not found"));

        TeamRole role = teamRoleRepository.findByTeam_IdAndName(teamId, roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        member.setRole(role);
        teamMemberRepository.save(member);

        return "Member role changed";
    }

    private User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
