package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.FIELD_VALIDATION_FAILED;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.VALIDATION_FAILED;

import com.pivo.weev.backend.rest.model.error.AlertRest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AlertRestFactory {

    public AlertRest create(String titleCode, String messageCode) {
        return new AlertRest(titleCode, messageCode);
    }

    public AlertRest validationFailed(String messageCode) {
        return new AlertRest(VALIDATION_FAILED, messageCode);
    }

    public AlertRest fieldValidationFailed(String messageCode) {
        return new AlertRest(FIELD_VALIDATION_FAILED, messageCode);
    }
}
