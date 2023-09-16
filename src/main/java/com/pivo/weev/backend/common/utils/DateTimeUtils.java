package com.pivo.weev.backend.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtils {

    public static Instant toInstant(LocalDateTime localDateTime, String localTimeZoneId) {
        ZoneOffset offset = getOffset(localDateTime, localTimeZoneId);
        return localDateTime.toInstant(offset);
    }

    private static ZoneOffset getOffset(LocalDateTime localDateTime, String localTimeZoneId) {
        return ZoneId.of(localTimeZoneId)
                     .getRules()
                     .getOffset(localDateTime);
    }
}
