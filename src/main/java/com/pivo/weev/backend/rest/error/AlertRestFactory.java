package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.FIELD_VALIDATION_FAILED_ERROR;

import com.pivo.weev.backend.rest.model.error.AlertRest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AlertRestFactory {

    public AlertRest create(String title, String message) {
        return new AlertRest(title, message);
    }

    public AlertRest fieldValidationFailed(String fieldError) {
        return new AlertRest(FIELD_VALIDATION_FAILED_ERROR, fieldError);
    }
}
