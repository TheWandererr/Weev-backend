package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.common.utils.Constants.Symbols.DOT;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.NOT_FOUND;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.VALIDATION_FAILED;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.FLOW_INTERRUPTED;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.model.error.Error;
import com.pivo.weev.backend.rest.model.exception.MissingCookieException;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.utils.Constants.ResponseDetails;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
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
        Error error = errorFactory.forbidden();
        BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
        logger.error(applicationLoggingHelper.buildLoggingError(body, accessDeniedException.getMessage()));
        return ResponseEntity.status(FORBIDDEN)
                             .body(body);
    }

    @ExceptionHandler(value = {MissingCookieException.class, InvalidCookieException.class})
    public ResponseEntity<BaseResponse> handleMissingCookieException(RuntimeException runtimeException) {
        Error error = errorFactory.forbidden();
        BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
        logger.error(applicationLoggingHelper.buildLoggingError(body, runtimeException.getMessage()));
        return ResponseEntity.status(FORBIDDEN)
                             .body(body);
    }

    @ExceptionHandler(value = InternalAuthenticationServiceException.class)
    public ResponseEntity<BaseResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException exception) {
        Error error = errorFactory.unauthorized(exception.getMessage());
        BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
        logger.error(applicationLoggingHelper.buildLoggingError(body, exception.getMessage()));
        return ResponseEntity.status(UNAUTHORIZED)
                             .body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String messageCode = ofNullable(exception.getBindingResult().getFieldError())
                .map(error -> StringUtils.join(error.getField(), DOT, error.getDefaultMessage()))
                .orElse(null);
        Error error = errorFactory.create(VALIDATION_FAILED, messageCode);
        BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
        logger.error(applicationLoggingHelper.buildLoggingError(body, null));
        return ResponseEntity.badRequest()
                             .body(body);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<BaseResponse> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException) {
        Error error = errorFactory.create(NOT_FOUND, resourceNotFoundException.getMessage());
        BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
        logger.error(applicationLoggingHelper.buildLoggingError(body, null));
        return ResponseEntity.badRequest()
                             .body(body);
    }

    @ExceptionHandler(value = {ReasonableException.class})
    public ResponseEntity<BaseResponse> handleReasonableException(ReasonableException reasonableException) {
        Error error = errorFactory.create(reasonableException.getErrorCode(), FLOW_INTERRUPTED);
        BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR, reasonableException.buildDetails());
        logger.error(applicationLoggingHelper.buildLoggingError(body, null));
        return ResponseEntity.status(reasonableException.getHttpStatus())
                             .body(body);
    }
}
