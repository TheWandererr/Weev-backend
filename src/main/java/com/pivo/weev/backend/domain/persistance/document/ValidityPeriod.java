package com.pivo.weev.backend.domain.persistance.document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidityPeriod {

    private Integer retryAfterSeconds;
    private Integer expiresAfterHours;

}
