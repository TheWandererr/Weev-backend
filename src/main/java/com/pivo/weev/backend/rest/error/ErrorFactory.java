package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.FORBIDDEN;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.UNAUTHORIZED;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.NOT_ENOUGH_PERMISSIONS;

import com.pivo.weev.backend.rest.model.error.ErrorRest;
import org.springframework.stereotype.Component;

@Component
public class ErrorFactory {

    public ErrorRest create(String code, String messageCode) {
        return new ErrorRest(code, messageCode);
    }

    public ErrorRest forbidden() {
        return new ErrorRest(FORBIDDEN, NOT_ENOUGH_PERMISSIONS);
    }

    public ErrorRest unauthorized(String messageCode) {
        return new ErrorRest(UNAUTHORIZED, messageCode);
    }
}
