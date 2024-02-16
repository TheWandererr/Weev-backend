package com.pivo.weev.backend.rest.handler;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getLoginDetails;
import static com.pivo.weev.backend.rest.utils.HttpServletUtils.writeResponse;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.OK;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.domain.model.auth.AuthTokens;
import com.pivo.weev.backend.domain.model.auth.LoginDetails;
import com.pivo.weev.backend.domain.service.auth.AuthTokensDetailsService;
import com.pivo.weev.backend.domain.service.auth.AuthTokensService;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.mapping.rest.UserSnapshotRestMapper;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.model.response.LoginResponse;
import com.pivo.weev.backend.rest.model.user.UserSnapshotRest;
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
    private final AuthTokensService authTokensService;
    private final AuthTokensDetailsService authTokensDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        handleSuccessLogin(response, authentication);
    }

    private void handleSuccessLogin(HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            LoginDetails loginDetails = getLoginDetails(authentication);
            AuthTokens authTokens = authTokensService.generateTokens(loginDetails);
            updateOrCreateTokenDetails(loginDetails, authTokens);
            UserSnapshotRest user = getMapper(UserSnapshotRestMapper.class).map(authTokens.getAccessToken());
            LoginResponse loginResponse = new LoginResponse(authTokens.getAccessTokenValue(), authTokens.getRefreshTokenValue(), user);
            writeResponse(loginResponse, response, OK, mapper);
        } catch (Exception exception) {
            LOGGER.error(applicationLoggingHelper.buildLoggingError(exception, null, false));
            BaseResponse loginResponse = new BaseResponse(ResponseMessage.UNAUTHORIZED);
            writeResponse(loginResponse, response, HttpStatus.UNAUTHORIZED, mapper);
        }
    }

    private void updateOrCreateTokenDetails(LoginDetails loginDetails, AuthTokens authTokens) {
        boolean updated = authTokensDetailsService.updateTokensDetails(loginDetails, authTokens);
        if (!updated) {
            authTokensDetailsService.createTokensDetails(loginDetails, authTokens);
        }
    }
}
