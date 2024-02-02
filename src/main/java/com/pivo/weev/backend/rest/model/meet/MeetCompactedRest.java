package com.pivo.weev.backend.rest.model.meet;

import static com.pivo.weev.backend.rest.utils.Constants.DateTimePatterns.YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pivo.weev.backend.rest.model.user.UserSnapshotRest;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetCompactedRest extends MeetSnapshotRest {

    private UserSnapshotRest creator;
    private String category;
    private String subcategory;
    private LocationRest location;
    private int membersLimit;
    private int membersCount;
    @JsonFormat(pattern = YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN)
    private LocalDateTime localStartDateTime;
    private String startTimeZoneId;
    private String status;
    private boolean ended;
    private boolean started;
}
