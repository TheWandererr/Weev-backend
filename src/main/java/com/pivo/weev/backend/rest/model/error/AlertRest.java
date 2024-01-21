package com.pivo.weev.backend.rest.model.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlertRest {

    private final String title;
    private final String message;
}
