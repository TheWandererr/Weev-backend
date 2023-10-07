package com.pivo.weev.backend.rest.model.event;

import static com.pivo.weev.backend.rest.utils.Constants.DateTimePatterns.YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDetailedRest extends EventCompactedRest {

    private String description;
    private EntryFeeRest entryFee;
    private RestrictionsRest restrictions;
    @JsonFormat(pattern = YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN)
    private LocalDateTime localEndDateTime;
    private String endTimeZoneId;
    private Instant utcStartDateTime;
    private Instant utcEndDateTime;
    private boolean member;
}
