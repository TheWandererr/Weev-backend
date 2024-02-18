package com.pivo.weev.backend.rest.handler;

import static com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage.FORBIDDEN;
import static com.pivo.weev.backend.rest.utils.Constants.ResponseDetails.TITLE;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PERMISSIONS_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.error.AlertRestFactory;
import com.pivo.weev.backend.rest.model.error.AlertRest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

@RequiredArgsConstructor
@Slf4j
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    private final ObjectMapper mapper;
    private final ApplicationLoggingHelper applicationLoggingHelper;
    private final AlertRestFactory alertRestFactory;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        AlertRest alert = alertRestFactory.create(PERMISSIONS_ERROR + TITLE, PERMISSIONS_ERROR);
        BaseResponse loginResponse = new BaseResponse(alert, FORBIDDEN);
        log.error(applicationLoggingHelper.buildLoggingError(accessDeniedException, null, false));
        writeResponse(loginResponse, response, HttpStatus.FORBIDDEN, mapper);
    }
}
