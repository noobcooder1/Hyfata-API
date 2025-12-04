package kr.hyfata.rest.api.dto.agora.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrivacySettingsRequest {

    private String profileVisibility;

    private String phoneVisibility;

    private String birthdayVisibility;

    private Boolean allowFriendRequests;

    private Boolean allowGroupInvites;

    private Boolean showOnlineStatus;

    private Integer sessionTimeout;
}
