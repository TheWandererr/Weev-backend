package com.pivo.weev.backend.rest.handler;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.rest.error.ErrorFactory;
import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.model.error.Error;
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
import org.springframework.security.access.AccessDeniedException;

@RequiredArgsConstructor
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccessDeniedHandler.class);

  private final ObjectMapper restResponseMapper;
  private final ApplicationLoggingHelper applicationLoggingHelper;
  private final ErrorFactory errorFactory;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    Error error = errorFactory.create(ErrorCodes.PERMISSIONS_ERROR, accessDeniedException.getMessage());
    BaseResponse loginResponse = new BaseResponse(error, ResponseMessage.FORBIDDEN);
    LOGGER.error(applicationLoggingHelper.buildLoggingError(accessDeniedException, null, false));
    writeResponse(loginResponse, response, HttpStatus.FORBIDDEN, restResponseMapper);
  }
}
