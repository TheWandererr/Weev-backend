package com.pivo.weev.backend.rest.handler;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getLoginDetails;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.OK;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.domain.model.auth.OAuthTokenDetails;
import com.pivo.weev.backend.domain.service.auth.OAuthTokenService;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.mapping.domain.OAuthTokenDetailsMapper;
import com.pivo.weev.backend.rest.model.auth.AuthTokens;
import com.pivo.weev.backend.rest.model.auth.LoginDetails;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.model.response.LoginResponse;
import com.pivo.weev.backend.rest.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

@RequiredArgsConstructor
public class AuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

    private final ObjectMapper mapper;
    private final ApplicationLoggingHelper applicationLoggingHelper;
    private final AuthService authService;
    private final OAuthTokenService oAuthTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        handleSuccessLogin(response, authentication);
    }

    private void handleSuccessLogin(HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            LoginDetails loginDetails = getLoginDetails(authentication);
            AuthTokens authTokens = authService.generateTokens(authentication);
            updateTokenDetails(loginDetails, authTokens);
            LoginResponse loginResponse = new LoginResponse(authTokens.getAccessTokenValue(), authTokens.getRefreshTokenValue());
            writeResponse(loginResponse, response, OK, mapper);
        } catch (Exception exception) {
            LOGGER.error(applicationLoggingHelper.buildLoggingError(exception, null, false));
            BaseResponse loginResponse = new BaseResponse(ResponseMessage.UNAUTHORIZED);
            writeResponse(loginResponse, response, HttpStatus.UNAUTHORIZED, mapper);
        }
    }

    private void updateTokenDetails(LoginDetails loginDetails, AuthTokens authTokens) {
        boolean updated = oAuthTokenService.updateTokenDetails(
                loginDetails.getUserId(),
                loginDetails.getDeviceId(),
                loginDetails.getSerial(),
                authTokens.getRefreshToken().getExpiresAt()
        );
        if (!updated) {
            OAuthTokenDetails tokenDetails = getMapper(OAuthTokenDetailsMapper.class).map(loginDetails, authTokens);
            oAuthTokenService.saveTokenDetails(tokenDetails);
        }
    }
}
