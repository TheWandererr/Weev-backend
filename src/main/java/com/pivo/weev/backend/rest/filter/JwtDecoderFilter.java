package com.pivo.weev.backend.rest.filter;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getAuthorizationValue;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.TOKEN_ERROR;
import static com.pivo.weev.backend.utils.Constants.Symbols.COLON;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.domain.service.jwt.JwtHolder;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.error.PopupRestFactory;
import com.pivo.weev.backend.rest.model.error.PopupRest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;

@AllArgsConstructor
public class JwtDecoderFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper;
    private final JwtHolder jwtHolder;
    private final JwtDecoder jwtDecoder;
    private final PopupRestFactory popupRestFactory;
    private final ApplicationLoggingHelper applicationLoggingHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = getAuthorizationValue(request);
        if (isNotBlank(authorization)) {
            try {
                Jwt jwt = jwtDecoder.decode(authorization);
                jwtHolder.setToken(jwt);
            } catch (Exception exception) {
                handleUnauthorized(response, exception.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void handleUnauthorized(HttpServletResponse response, String failure) throws IOException {
        PopupRest error = popupRestFactory.unauthorized();
        BaseResponse errorResponse = new BaseResponse(error, ResponseMessage.UNAUTHORIZED);
        logger.error(applicationLoggingHelper.buildLoggingError(errorResponse, join(TOKEN_ERROR, COLON, failure), false));
        writeResponse(errorResponse, response, HttpStatus.UNAUTHORIZED, mapper);
    }
}
