package kr.hyfata.rest.api.dto.agora.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBirthdayReminderRequest {

    private Boolean birthdayReminderEnabled;

    private Integer birthdayReminderDaysBefore;
}
