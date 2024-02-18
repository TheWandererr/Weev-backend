package com.pivo.weev.backend.integration.mapping;

import com.pivo.weev.backend.utils.DateTimeUtils;
import java.time.Instant;
import org.mapstruct.Mapper;

@Mapper
public interface DateTimeMapper {

    default Long toEpochMilli(Instant instant) {
        return DateTimeUtils.toEpochMilli(instant);
    }

    default Instant toInstant(Long epochMilli) {
        return DateTimeUtils.toInstant(epochMilli);
    }
}
