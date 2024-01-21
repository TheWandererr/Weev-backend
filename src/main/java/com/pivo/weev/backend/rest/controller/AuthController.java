package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.rest.utils.HttpServletUtils.getAuthorizationValue;

import com.pivo.weev.backend.rest.model.auth.AuthTokens;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.rest.model.response.LoginResponse;
import com.pivo.weev.backend.rest.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/tokens/refresh")
    @PreAuthorize("hasAnyAuthority('SCOPE_refresh')")
    public LoginResponse refreshToken(HttpServletRequest httpServletRequest) {
        String token = getAuthorizationValue(httpServletRequest);
        AuthTokens authTokens = authService.refreshTokens(token);
        return new LoginResponse(authTokens.getAccessTokenValue(), authTokens.getRefreshTokenValue());
    }

    @PostMapping("/logout")
    public BaseResponse logout() {
        authService.logout();
        return new BaseResponse(ResponseMessage.SUCCESS);
    }
}
