package kr.hyfata.rest.api.service.agora;

import kr.hyfata.rest.api.dto.agora.team.TeamProfileResponse;
import kr.hyfata.rest.api.dto.agora.team.CreateTeamProfileRequest;

public interface AgoraTeamProfileService {

    TeamProfileResponse getMyTeamProfile(String userEmail, Long teamId);

    TeamProfileResponse createTeamProfile(String userEmail, Long teamId, CreateTeamProfileRequest request);

    TeamProfileResponse updateTeamProfile(String userEmail, Long teamId, String displayName, String profileImage);

    TeamProfileResponse updateTeamProfileImage(String userEmail, Long teamId, String profileImage);

    TeamProfileResponse getTeamMemberProfile(Long teamId, Long userId);
}
