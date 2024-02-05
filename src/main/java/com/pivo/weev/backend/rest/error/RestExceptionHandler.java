package com.pivo.weev.backend.rest.error;

import static com.pivo.weev.backend.rest.utils.Constants.PopupButtons.OK;
import static com.pivo.weev.backend.utils.Constants.Symbols.DOT;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.model.error.AlertRest;
import com.pivo.weev.backend.rest.model.error.NotificationRest;
import com.pivo.weev.backend.rest.model.error.PopupRest;
import com.pivo.weev.backend.rest.model.exception.MissingCookieException;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.utils.Constants.ResponseDetails;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
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

    private final PopupRestFactory popupRestFactory;
    private final AlertRestFactory alertRestFactory;
    private final NotificationRestFactory notificationRestFactory;
    private final ApplicationLoggingHelper applicationLoggingHelper;

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception exception) {
        PopupRest popupRest = popupRestFactory.somethingWentWrong();
        BaseResponse body = new BaseResponse(popupRest, ResponseMessage.ERROR, Map.of(ResponseDetails.REASON, exception.getMessage()));
        logger.error(applicationLoggingHelper.buildLoggingError(body, null));
        return ResponseEntity.internalServerError()
                             .body(body);
    }

    @ExceptionHandler(value = {AccessDeniedException.class, AuthorizationServiceException.class})
    public ResponseEntity<BaseResponse> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
        PopupRest error = popupRestFactory.forbidden();
        BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
        logger.error(applicationLoggingHelper.buildLoggingError(accessDeniedException, null));
        return ResponseEntity.status(FORBIDDEN)
                             .body(body);
    }

    @ExceptionHandler(value = {MissingCookieException.class, InvalidCookieException.class})
    public ResponseEntity<BaseResponse> handleMissingCookieException(RuntimeException runtimeException) {
        PopupRest error = popupRestFactory.forbidden();
        BaseResponse body = new BaseResponse(error, ResponseMessage.FORBIDDEN);
        logger.error(applicationLoggingHelper.buildLoggingError(body, runtimeException.getMessage()));
        return ResponseEntity.status(FORBIDDEN)
                             .body(body);
    }

    @ExceptionHandler(value = InternalAuthenticationServiceException.class)
    public ResponseEntity<BaseResponse> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException exception) {
        NotificationRest notification = notificationRestFactory.forbidden(exception.getMessage());
        BaseResponse body = new BaseResponse(notification, ResponseMessage.FORBIDDEN);
        logger.error(applicationLoggingHelper.buildLoggingError(exception, null));
        return ResponseEntity.status(FORBIDDEN)
                             .body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String path = ofNullable(exception.getBindingResult().getFieldError())
                .map(error -> StringUtils.join(error.getField(), DOT, error.getDefaultMessage()))
                .orElse(null);
        AlertRest alert = alertRestFactory.fieldValidationFailed(path);
        BaseResponse body = new BaseResponse(alert, ResponseMessage.ERROR);
        logger.error(applicationLoggingHelper.buildLoggingError(body, null));
        return ResponseEntity.badRequest()
                             .body(body);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<BaseResponse> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException) {
        PopupRest error = popupRestFactory.resourceNotFound(resourceNotFoundException.getCode());
        BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR);
        logger.error(applicationLoggingHelper.buildLoggingError(resourceNotFoundException, null));
        return ResponseEntity.badRequest()
                             .body(body);
    }

    @ExceptionHandler(value = {FlowInterruptedException.class})
    public ResponseEntity<BaseResponse> handleFlowInterruptedException(FlowInterruptedException flowInterruptedException) {
        PopupRest error = popupRestFactory.create(flowInterruptedException.getCode(), flowInterruptedException.getReason(), List.of(OK));
        BaseResponse body = new BaseResponse(error, ResponseMessage.ERROR, flowInterruptedException.buildDetails());
        logger.error(applicationLoggingHelper.buildLoggingError(body, null));
        HttpStatus httpStatus = ofNullable(flowInterruptedException.getHttpStatus()).orElse(BAD_REQUEST);
        return ResponseEntity.status(httpStatus)
                             .body(body);
    }
}
