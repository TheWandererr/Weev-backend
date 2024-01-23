package com.pivo.weev.backend.rest.filter;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getDeviceId;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_TOKEN;
import static com.pivo.weev.backend.utils.Constants.Symbols.COLON;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.join;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.error.PopupRestFactory;
import com.pivo.weev.backend.rest.model.auth.JwtVerificationResult;
import com.pivo.weev.backend.rest.model.error.PopupRest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.service.security.JwtAuthenticityVerifier;
import com.pivo.weev.backend.rest.service.security.JwtHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
public class JwtAuthenticityVerifierFilter extends OncePerRequestFilter {

    private final PopupRestFactory popupRestFactory;
    private final ObjectMapper mapper;
    private final ApplicationLoggingHelper applicationLoggingHelper;
    private final JwtAuthenticityVerifier jwtAuthenticityVerifier;
    private final JwtHolder jwtHolder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Jwt token = jwtHolder.getToken();
        if (isSkipRequest(token)) {
            filterChain.doFilter(request, response);
            return;
        } else {
            JwtVerificationResult verificationResult = jwtAuthenticityVerifier.verify(token, getDeviceId(request).orElse(null));
            if (!verificationResult.isSuccessful()) {
                handleUnauthorized(response, verificationResult.getFailure());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isSkipRequest(Jwt jwt) {
        return isNull(jwt); // TODO check this!!!
        /*if (HttpMethod.GET.matches(request.getMethod())) {
            if (matches(request, REFRESH_URI)) {
                return false;
            }
            return !matches(request, MODERATION_URI);
        }
        return matches(request, LOGIN_URI) || matches(request, EVENTS_SEARCH_URI);*/
    }

    private void handleUnauthorized(HttpServletResponse response, String failure) throws IOException {
        PopupRest error = popupRestFactory.unauthorized();
        BaseResponse errorResponse = new BaseResponse(error, ResponseMessage.UNAUTHORIZED);
        logger.error(applicationLoggingHelper.buildLoggingError(errorResponse, join(INVALID_TOKEN, COLON, failure), false));
        writeResponse(errorResponse, response, HttpStatus.UNAUTHORIZED, mapper);
    }
}
