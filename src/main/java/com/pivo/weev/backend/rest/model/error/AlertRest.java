package com.pivo.weev.backend.rest.model.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlertRest {

    private final String titleCode;
    private final String messageCode;
}
