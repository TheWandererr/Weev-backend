package com.pivo.weev.backend.rest.handler;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.UNAUTHORIZED;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.rest.error.ErrorFactory;
import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.model.error.Error;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
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
    Error error = errorFactory.create(UNAUTHORIZED, exception.getMessage());
    BaseResponse loginResponse = new BaseResponse(error, ResponseMessage.UNAUTHORIZED);
    LOGGER.error(applicationLoggingHelper.buildLoggingError(exception, null, false));
    writeResponse(loginResponse, response, HttpStatus.UNAUTHORIZED, restResponseMapper);
  }
}
