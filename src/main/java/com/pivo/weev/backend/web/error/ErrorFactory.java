package com.pivo.weev.backend.web.error;

import static com.pivo.weev.backend.web.utils.Constants.ErrorCodes.AUTHORIZATION_REQUIRED_ERROR;
import static com.pivo.weev.backend.web.utils.Constants.ErrorCodes.FORBIDDEN_ERROR;
import static com.pivo.weev.backend.web.utils.Constants.ErrorMessages.PERMISSION_DENIED;

import com.pivo.weev.backend.web.model.error.Error;
import org.springframework.stereotype.Component;

@Component
public class ErrorFactory {

  public Error create(String code, String message) {
    return new Error(code, message);
  }

  public Error forbidden(RuntimeException runtimeException) {
    return new Error(FORBIDDEN_ERROR, runtimeException.getMessage());
  }

  public Error unauthorized(String message) {
    return new Error(AUTHORIZATION_REQUIRED_ERROR, message);
  }
}
