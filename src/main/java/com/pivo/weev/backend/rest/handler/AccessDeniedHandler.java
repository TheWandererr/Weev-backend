package com.pivo.weev.backend.rest.handler;

import static com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage.FORBIDDEN;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.NO_PERMISSIONS;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.NOT_ENOUGH_PERMISSIONS;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.rest.error.AlertRestFactory;
import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.model.error.AlertRest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

@RequiredArgsConstructor
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessDeniedHandler.class);

    private final ObjectMapper restResponseMapper;
    private final ApplicationLoggingHelper applicationLoggingHelper;
    private final AlertRestFactory alertRestFactory;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        AlertRest alert = alertRestFactory.create(NO_PERMISSIONS, NOT_ENOUGH_PERMISSIONS);
        BaseResponse loginResponse = new BaseResponse(alert, FORBIDDEN);
        LOGGER.error(applicationLoggingHelper.buildLoggingError(accessDeniedException, null, false));
        writeResponse(loginResponse, response, HttpStatus.FORBIDDEN, restResponseMapper);
    }
}
