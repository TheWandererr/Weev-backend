package com.pivo.weev.backend.domain.service;

import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.TIME_ZONE_ID_NOT_FOUND_ERROR;

import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import java.time.ZoneId;
import net.iakovlev.timeshape.TimeZoneEngine;
import org.springframework.stereotype.Service;

@Service
public class TimeZoneService {

    private static final TimeZoneEngine TIME_ZONE_ENGINE = TimeZoneEngine.initialize();

    public ZoneId getZoneId(double latitude, double longitude) {
        return TIME_ZONE_ENGINE.query(latitude, longitude)
                               .orElseThrow(() -> new ReasonableException(TIME_ZONE_ID_NOT_FOUND_ERROR));
    }
}
