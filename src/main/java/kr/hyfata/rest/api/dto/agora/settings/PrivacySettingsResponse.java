package kr.hyfata.rest.api.dto.agora.settings;

import kr.hyfata.rest.api.entity.agora.UserSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivacySettingsResponse {

    private String profileVisibility;

    private String phoneVisibility;

    private String birthdayVisibility;

    private Boolean allowFriendRequests;

    private Boolean allowGroupInvites;

    private Boolean showOnlineStatus;

    private Integer sessionTimeout;

    public static PrivacySettingsResponse from(UserSettings settings) {
        return PrivacySettingsResponse.builder()
                .profileVisibility(settings.getProfileVisibility().toString())
                .phoneVisibility(settings.getPhoneVisibility().toString())
                .birthdayVisibility(settings.getBirthdayVisibility().toString())
                .allowFriendRequests(settings.getAllowFriendRequests())
                .allowGroupInvites(settings.getAllowGroupInvites())
                .showOnlineStatus(settings.getShowOnlineStatus())
                .sessionTimeout(settings.getSessionTimeout())
                .build();
    }
}
