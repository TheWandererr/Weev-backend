package com.pivo.weev.backend.web.filter;

import static com.pivo.weev.backend.domain.utils.JwtUtils.getDeviceId;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getSerial;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getUserId;
import static com.pivo.weev.backend.web.utils.HttpServletUtils.getAuthorizationValue;
import static com.pivo.weev.backend.web.utils.HttpServletUtils.writeResponse;
import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.dao.model.OAuthTokenDetailsJpa;
import com.pivo.weev.backend.dao.repository.wrapper.OAuthTokenDetailsRepositoryWrapper;
import com.pivo.weev.backend.web.error.ErrorFactory;
import com.pivo.weev.backend.web.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.web.model.error.Error;
import com.pivo.weev.backend.web.model.response.BaseResponse;
import com.pivo.weev.backend.web.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.web.utils.Constants.Api;
import com.pivo.weev.backend.web.utils.Constants.ErrorMessages;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
public class JWTVerifierFilter extends OncePerRequestFilter {

  private final ErrorFactory errorFactory;
  private final ObjectMapper restResponseMapper;
  private final ApplicationLoggingHelper applicationLoggingHelper;
  private final JwtDecoder jwtDecoder;
  private final OAuthTokenDetailsRepositoryWrapper oAuthTokenDetailsRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (isSkipRequest(request)) {
      filterChain.doFilter(request, response);
      return;
    } else {
      Jwt jwt = jwtDecoder.decode(getAuthorizationValue(request));
      OAuthTokenDetailsJpa tokenDetails = oAuthTokenDetailsRepository.findByUserIdAndDeviceId(getUserId(jwt), getDeviceId(jwt));
      if (isNull(tokenDetails)) {
        handleUnauthorized(response);
        return;
      }
      if (!StringUtils.equals(getSerial(jwt), tokenDetails.getSerial())) {
        handleUnauthorized(response);
        return;
      }
      if (!StringUtils.equals(getDeviceId(jwt), tokenDetails.getDeviceId())) {
        handleUnauthorized(response);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  private boolean isSkipRequest(HttpServletRequest request) {
    if (HttpMethod.GET.matches(request.getMethod())) {
      return !request.getRequestURI().endsWith(Api.REFRESH_URI);
    }
    return request.getRequestURI().endsWith(Api.LOGIN_URI);
  }

  private void handleUnauthorized(HttpServletResponse response) throws IOException {
    Error error = errorFactory.unauthorized(ErrorMessages.TOKEN_REVOKED);
    BaseResponse errorResponse = new BaseResponse(error, ResponseMessage.UNAUTHORIZED);
    logger.error(applicationLoggingHelper.buildLoggingError(errorResponse, null, false));
    writeResponse(errorResponse, response, HttpStatus.UNAUTHORIZED, restResponseMapper);
  }
}
