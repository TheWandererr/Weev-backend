package com.pivo.weev.backend.rest.model.event;

import static com.pivo.weev.backend.rest.utils.Constants.DateTimePatterns.YYYY_DD_MM_HH_MM_DATE_TIME_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pivo.weev.backend.rest.model.UserCompactedRest;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventPreviewRest {

    private Long id;
    private UserCompactedRest creator;
    private String header;
    private String category;
    private String subcategory;
    private LocationRest location;
    private Integer membersLimit;
    private Integer membersCount;
    private String description;
    private ImageRest photo;
    private EntryFeeRest entryFee;
    private RestrictionsRest restrictions;
    @JsonFormat(pattern = YYYY_DD_MM_HH_MM_DATE_TIME_PATTERN)
    private LocalDateTime localStartDateTime;
    private String startTimeZoneId;
    @JsonFormat(pattern = YYYY_DD_MM_HH_MM_DATE_TIME_PATTERN)
    private LocalDateTime localEndDateTime;
    private String endTimeZoneId;
}
