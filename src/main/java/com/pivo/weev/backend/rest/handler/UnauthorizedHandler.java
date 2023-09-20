package com.pivo.weev.backend.rest.handler;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.rest.error.ErrorFactory;
import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.model.error.ErrorRest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.utils.Constants.ErrorCodes;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@RequiredArgsConstructor
public class UnauthorizedHandler implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnauthorizedHandler.class);

    private final ObjectMapper restResponseMapper;
    private final ApplicationLoggingHelper applicationLoggingHelper;
    private final ErrorFactory errorFactory;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorRest error = errorFactory.create(ErrorCodes.UNAUTHORIZED, authException.getMessage());
        BaseResponse loginResponse = new BaseResponse(error, ResponseMessage.UNAUTHORIZED);
        LOGGER.error(applicationLoggingHelper.buildLoggingError(authException, null, false));
        writeResponse(loginResponse, response, HttpStatus.UNAUTHORIZED, restResponseMapper);
    }
}
