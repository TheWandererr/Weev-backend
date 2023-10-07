package com.pivo.weev.backend.rest.model.event;

import static com.pivo.weev.backend.rest.utils.Constants.DateTimePatterns.YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pivo.weev.backend.rest.model.user.UserCompactedRest;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCompactedRest {

    private Long id;
    private UserCompactedRest creator;
    private String header;
    private String category;
    private String subcategory;
    private LocationRest location;
    private int membersLimit;
    private int membersCount;
    private ImageRest photo;
    @JsonFormat(pattern = YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN)
    private LocalDateTime localStartDateTime;
    private String startTimeZoneId;
    private String status;
    private boolean ended;
    private boolean started;
}
