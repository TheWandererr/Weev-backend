package com.pivo.weev.backend.integration.mapping;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.Date;
import org.mapstruct.Mapper;

@Mapper
public interface DateTimeMapper {

    default Instant map(Date source) {
        if (isNull(source)) {
            return null;
        }
        return source.toInstant();
    }

    default Date map(Instant source) {
        return Date.from(source);
    }
}
