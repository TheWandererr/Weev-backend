package com.pivo.weev.backend.rest.handler;

import static com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage.UNAUTHORIZED;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.error.NotificationRestFactory;
import com.pivo.weev.backend.rest.model.error.NotificationRest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@RequiredArgsConstructor
@Slf4j
public class UnauthorizedHandler implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;
    private final ApplicationLoggingHelper applicationLoggingHelper;
    private final NotificationRestFactory notificationRestFactory;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        NotificationRest notification = notificationRestFactory.unauthorized();
        BaseResponse loginResponse = new BaseResponse(notification, UNAUTHORIZED);
        log.error(applicationLoggingHelper.buildLoggingError(authException, null, false));
        writeResponse(loginResponse, response, HttpStatus.UNAUTHORIZED, mapper);
    }
}
