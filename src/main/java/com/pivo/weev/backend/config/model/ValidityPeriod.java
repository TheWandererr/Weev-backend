package com.pivo.weev.backend.config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidityPeriod {

    private Integer retryAfterSeconds;
    private Integer expiresAfterHours;

}
