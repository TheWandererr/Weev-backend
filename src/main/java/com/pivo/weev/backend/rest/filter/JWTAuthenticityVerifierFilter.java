package com.pivo.weev.backend.rest.filter;

import static com.pivo.weev.backend.rest.utils.Constants.Api.LOGIN_URI;
import static com.pivo.weev.backend.rest.utils.Constants.Api.MODERATION_URI;
import static com.pivo.weev.backend.rest.utils.Constants.Api.REFRESH_URI;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorMessageCodes.INVALID_TOKEN;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getAuthorizationValue;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getDeviceId;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.matches;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.rest.error.ErrorFactory;
import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.model.error.ErrorRest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.service.security.JWTAuthenticityVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
public class JWTAuthenticityVerifierFilter extends OncePerRequestFilter {

    private final ErrorFactory errorFactory;
    private final ObjectMapper restResponseMapper;
    private final ApplicationLoggingHelper applicationLoggingHelper;
    private final JWTAuthenticityVerifier jwtAuthenticityVerifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isSkipRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        } else {
            Pair<Boolean, String> verificationResult = jwtAuthenticityVerifier.verify(getAuthorizationValue(request), getDeviceId(request).orElse(null));
            if (isNotTrue(verificationResult.getLeft())) {
                handleUnauthorized(response, verificationResult.getRight());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isSkipRequest(HttpServletRequest request) {
        if (HttpMethod.GET.matches(request.getMethod())) {
            if (matches(request, REFRESH_URI)) {
                return false;
            }
            return !matches(request, MODERATION_URI);
        }
        return matches(request, LOGIN_URI);
    }

    private void handleUnauthorized(HttpServletResponse response, String failure) throws IOException {
        ErrorRest error = errorFactory.unauthorized(INVALID_TOKEN);
        BaseResponse errorResponse = new BaseResponse(error, ResponseMessage.UNAUTHORIZED);
        logger.error(applicationLoggingHelper.buildLoggingError(errorResponse, failure, false));
        writeResponse(errorResponse, response, HttpStatus.UNAUTHORIZED, restResponseMapper);
    }
}
