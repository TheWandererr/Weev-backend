package com.pivo.weev.backend.rest.error;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.model.error.Error;
import com.pivo.weev.backend.rest.model.exception.MissingCookieException;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.utils.Constants.ResponseDetails;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RequiredArgsConstructor
@ControllerAdvice(annotations = {Service.class, Component.class, RestController.class})
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private final ErrorFactory errorFactory;
  private final ApplicationLoggingHelper applicationLoggingHelper;

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<BaseResponse> handleException(Exception exception) {
    BaseResponse body = new BaseResponse(null, ResponseMessage.ERROR, Map.of(ResponseDetails.REASON, exception.getMessage()));
    logger.error(applicationLoggingHelper.buildLoggingError(body, null));
    return ResponseEntity.internalServerError()
                         .body(body);
  }

  @ExceptionHandler(value = AccessDeniedException.class)
  public ResponseEntity<BaseResponse> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
    Error error = errorFactory.forbidden(accessDeniedException);
    BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
    logger.error(applicationLoggingHelper.buildLoggingError(body, null));
    return ResponseEntity.status(FORBIDDEN)
                         .body(body);
  }

  @ExceptionHandler(value = {MissingCookieException.class, InvalidCookieException.class})
  public ResponseEntity<BaseResponse> handleMissingCookieException(RuntimeException runtimeException) {
    Error error = errorFactory.forbidden(runtimeException);
    BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
    logger.error(applicationLoggingHelper.buildLoggingError(body, null));
    return ResponseEntity.status(FORBIDDEN)
                         .body(body);
  }

  @ExceptionHandler(value = InternalAuthenticationServiceException.class)
  public ResponseEntity<BaseResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException exception) {
    Error error = errorFactory.unauthorized(exception.getMessage());
    BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
    logger.error(applicationLoggingHelper.buildLoggingError(body, null));
    return ResponseEntity.status(UNAUTHORIZED)
                         .body(body);
  }
}
