package com.pivo.weev.backend.web.handler;

import static com.pivo.weev.backend.web.utils.Constants.ErrorCodes.AUTHENTICATION_FAILED_ERROR;
import static com.pivo.weev.backend.web.utils.HttpServletUtils.writeResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.web.error.ErrorFactory;
import com.pivo.weev.backend.web.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.web.model.error.Error;
import com.pivo.weev.backend.web.model.response.BaseResponse;
import com.pivo.weev.backend.web.model.response.BaseResponse.ResponseMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFailureHandler.class);

  private final ObjectMapper restResponseMapper;
  private final ApplicationLoggingHelper applicationLoggingHelper;
  private final ErrorFactory errorFactory;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {
    handleFailedLogin(response, exception);
  }

  private void handleFailedLogin(HttpServletResponse response, AuthenticationException exception) throws IOException {
    Error error = errorFactory.create(AUTHENTICATION_FAILED_ERROR, exception.getMessage());
    BaseResponse loginResponse = new BaseResponse(error, ResponseMessage.UNAUTHORIZED);
    LOGGER.error(applicationLoggingHelper.buildLoggingError(exception, null, false));
    writeResponse(loginResponse, response, HttpStatus.UNAUTHORIZED, restResponseMapper);
  }
}
