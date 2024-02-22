package com.pivo.weev.backend.utils;

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

    public static Instant toInstant(Long epochMilli) {
        return Instant.ofEpochMilli(epochMilli);
    }

    private static ZoneOffset getOffset(LocalDateTime localDateTime, String localTimeZoneId) {
        return ZoneId.of(localTimeZoneId)
                     .getRules()
                     .getOffset(localDateTime);
    }

    public static Long toEpochMilli(Instant instant) {
        return instant.toEpochMilli();
    }
}
