package com.pivo.weev.backend.rest.filter;

import static com.pivo.weev.backend.domain.utils.JwtUtils.getSerial;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getDeviceId;
import static com.pivo.weev.backend.rest.utils.Constants.Api.LOGIN_URI;
import static com.pivo.weev.backend.rest.utils.Constants.Api.REFRESH_URI;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.INVALID_TOKEN;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getAuthorizationValue;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;
import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.dao.model.auth.OAuthTokenDetailsJpa;
import com.pivo.weev.backend.dao.repository.wrapper.OAuthTokenDetailsRepositoryWrapper;
import com.pivo.weev.backend.rest.error.ErrorFactory;
import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.model.error.Error;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.utils.HttpServletUtils;
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
import org.springframework.security.oauth2.jwt.JwtException;
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
      try {
        Jwt jwt = jwtDecoder.decode(getAuthorizationValue(request));
        OAuthTokenDetailsJpa tokenDetails = oAuthTokenDetailsRepository.findByUserIdAndDeviceId(getUserId(jwt), getDeviceId(jwt));
        if (isNull(tokenDetails)) {
          handleUnauthorized(response, null);
          return;
        }
        if (!StringUtils.equals(getSerial(jwt), tokenDetails.getSerial())) {
          handleUnauthorized(response, null);
          return;
        }
        String cookieDeviceId = HttpServletUtils.getDeviceId(request).orElse(null);
        if (!StringUtils.equals(cookieDeviceId, tokenDetails.getDeviceId())) {
          handleUnauthorized(response, null);
          return;
        }
      } catch (JwtException jwtException) {
        handleUnauthorized(response, jwtException.getMessage());
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  private boolean isSkipRequest(HttpServletRequest request) {
    if (HttpMethod.GET.matches(request.getMethod())) {
      return !request.getRequestURI().endsWith(REFRESH_URI);
    }
    return request.getRequestURI().endsWith(LOGIN_URI);
  }

  private void handleUnauthorized(HttpServletResponse response, String failure) throws IOException {
    Error error = errorFactory.unauthorized(INVALID_TOKEN);
    BaseResponse errorResponse = new BaseResponse(error, ResponseMessage.UNAUTHORIZED);
    logger.error(applicationLoggingHelper.buildLoggingError(errorResponse, failure, false));
    writeResponse(errorResponse, response, HttpStatus.UNAUTHORIZED, restResponseMapper);
  }
}
