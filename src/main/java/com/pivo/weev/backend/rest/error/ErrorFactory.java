package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.FORBIDDEN;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.UNAUTHORIZED;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.NOT_ENOUGH_PERMISSIONS;

import com.pivo.weev.backend.rest.model.error.Error;
import org.springframework.stereotype.Component;

@Component
public class ErrorFactory {

    public Error create(String code, String messageCode) {
        return new Error(code, messageCode);
    }

    public Error forbidden() {
        return new Error(FORBIDDEN, NOT_ENOUGH_PERMISSIONS);
    }

    public Error unauthorized(String messageCode) {
        return new Error(UNAUTHORIZED, messageCode);
    }
}
