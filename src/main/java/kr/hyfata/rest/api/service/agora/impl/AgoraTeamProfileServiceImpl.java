package kr.hyfata.rest.api.service.agora.impl;

import kr.hyfata.rest.api.dto.agora.team.TeamProfileResponse;
import kr.hyfata.rest.api.dto.agora.team.CreateTeamProfileRequest;
import kr.hyfata.rest.api.entity.User;
import kr.hyfata.rest.api.entity.agora.Team;
import kr.hyfata.rest.api.entity.agora.TeamProfile;
import kr.hyfata.rest.api.repository.UserRepository;
import kr.hyfata.rest.api.repository.agora.TeamRepository;
import kr.hyfata.rest.api.repository.agora.TeamProfileRepository;
import kr.hyfata.rest.api.service.agora.AgoraTeamProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AgoraTeamProfileServiceImpl implements AgoraTeamProfileService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamProfileRepository teamProfileRepository;

    @Override
    public TeamProfileResponse getMyTeamProfile(String userEmail, Long teamId) {
        User user = findUserByEmail(userEmail);
        Team team = findTeamById(teamId);

        // Verify user is member
        if (!isTeamMember(teamId, user.getId())) {
            throw new IllegalStateException("You are not a member of this team");
        }

        TeamProfile profile = teamProfileRepository.findByTeamIdAndUserId(teamId, user.getId())
                .orElseThrow(() -> new IllegalStateException("Team profile not found"));

        return TeamProfileResponse.from(profile);
    }

    @Override
    @Transactional
    public TeamProfileResponse createTeamProfile(String userEmail, Long teamId, CreateTeamProfileRequest request) {
        User user = findUserByEmail(userEmail);
        Team team = findTeamById(teamId);

        // Verify user is member
        if (!isTeamMember(teamId, user.getId())) {
            throw new IllegalStateException("You are not a member of this team");
        }

        // Check if profile already exists
        if (teamProfileRepository.existsByTeamIdAndUserId(teamId, user.getId())) {
            throw new IllegalStateException("Team profile already exists");
        }

        TeamProfile profile = TeamProfile.builder()
                .team(team)
                .user(user)
                .displayName(request.getDisplayName())
                .profileImage(request.getProfileImage())
                .build();

        TeamProfile savedProfile = teamProfileRepository.save(profile);
        return TeamProfileResponse.from(savedProfile);
    }

    @Override
    @Transactional
    public TeamProfileResponse updateTeamProfile(String userEmail, Long teamId, String displayName, String profileImage) {
        User user = findUserByEmail(userEmail);
        Team team = findTeamById(teamId);

        // Verify user is member
        if (!isTeamMember(teamId, user.getId())) {
            throw new IllegalStateException("You are not a member of this team");
        }

        TeamProfile profile = teamProfileRepository.findByTeamIdAndUserId(teamId, user.getId())
                .orElseThrow(() -> new IllegalStateException("Team profile not found"));

        if (displayName != null && !displayName.isEmpty()) {
            profile.setDisplayName(displayName);
        }

        if (profileImage != null) {
            profile.setProfileImage(profileImage);
        }

        TeamProfile updated = teamProfileRepository.save(profile);
        return TeamProfileResponse.from(updated);
    }

    @Override
    @Transactional
    public TeamProfileResponse updateTeamProfileImage(String userEmail, Long teamId, String profileImage) {
        User user = findUserByEmail(userEmail);
        Team team = findTeamById(teamId);

        // Verify user is member
        if (!isTeamMember(teamId, user.getId())) {
            throw new IllegalStateException("You are not a member of this team");
        }

        TeamProfile profile = teamProfileRepository.findByTeamIdAndUserId(teamId, user.getId())
                .orElseThrow(() -> new IllegalStateException("Team profile not found"));

        if (profileImage != null) {
            profile.setProfileImage(profileImage);
        }

        TeamProfile updated = teamProfileRepository.save(profile);
        return TeamProfileResponse.from(updated);
    }

    @Override
    public TeamProfileResponse getTeamMemberProfile(Long teamId, Long userId) {
        Team team = findTeamById(teamId);
        User user = findUserById(userId);

        // Verify user is member of team
        if (!isTeamMember(teamId, userId)) {
            throw new IllegalStateException("User is not a member of this team");
        }

        TeamProfile profile = teamProfileRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new IllegalStateException("Team profile not found"));

        return TeamProfileResponse.from(profile);
    }

    private User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
    }

    private boolean isTeamMember(Long teamId, Long userId) {
        return teamProfileRepository.existsByTeamIdAndUserId(teamId, userId);
    }
}
