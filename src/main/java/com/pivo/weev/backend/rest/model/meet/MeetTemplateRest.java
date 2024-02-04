package com.pivo.weev.backend.rest.model.meet;

import static com.pivo.weev.backend.rest.utils.Constants.DateTimePatterns.YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetTemplateRest {

    private String header;
    private String category;
    private String subcategory;
    private LocationRest location;
    private int membersLimit;
    private String description;
    private RestrictionsRest restrictions;
    @JsonFormat(pattern = YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN)
    private LocalDateTime localStartDateTime;
    private String startTimeZoneId;
    @JsonFormat(pattern = YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN)
    private LocalDateTime localEndDateTime;
    private String endTimeZoneId;
    private Instant utcStartDateTime;
    private Instant utcEndDateTime;

}
