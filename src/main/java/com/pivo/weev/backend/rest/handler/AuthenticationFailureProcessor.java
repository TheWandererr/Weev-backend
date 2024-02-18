package com.pivo.weev.backend.rest.handler;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.domain.model.exception.AuthenticationDeniedException;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.error.NotificationRestFactory;
import com.pivo.weev.backend.rest.model.error.NotificationRest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@RequiredArgsConstructor
@Slf4j
public class AuthenticationFailureProcessor implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;
    private final ApplicationLoggingHelper applicationLoggingHelper;
    private final NotificationRestFactory notificationRestFactory;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        boolean forbidden = exception instanceof AuthenticationDeniedException;
        NotificationRest notification = forbidden
                ? notificationRestFactory.forbidden(exception.getMessage())
                : notificationRestFactory.badCredentials();
        BaseResponse loginResponse = new BaseResponse(notification, forbidden ? ResponseMessage.FORBIDDEN : ResponseMessage.UNAUTHORIZED);
        log.error(applicationLoggingHelper.buildLoggingError(exception, null, false));
        writeResponse(loginResponse, response, forbidden ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED, mapper);
    }
}
