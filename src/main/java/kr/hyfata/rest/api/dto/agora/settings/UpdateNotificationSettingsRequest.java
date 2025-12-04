package kr.hyfata.rest.api.dto.agora.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationSettingsRequest {

    private Boolean pushEnabled;

    private Boolean messageNotification;

    private Boolean friendRequestNotification;

    private Boolean teamNotification;

    private Boolean noticeNotification;

    private Boolean soundEnabled;

    private Boolean vibrationEnabled;

    private LocalTime doNotDisturbStart;

    private LocalTime doNotDisturbEnd;

    private Boolean loginNotification;
}
