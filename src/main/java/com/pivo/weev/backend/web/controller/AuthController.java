package com.pivo.weev.backend.web.controller;

import static com.pivo.weev.backend.web.utils.HttpServletUtils.getAuthorizationValue;

import com.pivo.weev.backend.web.model.auth.JWTPair;
import com.pivo.weev.backend.web.model.response.BaseResponse;
import com.pivo.weev.backend.web.model.response.BaseResponse.ResponseMessage;
import com.pivo.weev.backend.web.model.response.LoginResponse;
import com.pivo.weev.backend.web.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weev/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @GetMapping("/refresh")
  @PreAuthorize("hasAnyAuthority('SCOPE_refresh')")
  public LoginResponse refreshToken(HttpServletRequest httpServletRequest) {
    String token = getAuthorizationValue(httpServletRequest);
    JWTPair jwtPair = authService.refreshAuthentication(token);
    return new LoginResponse(jwtPair.getAccessToken().getTokenValue(), jwtPair.getRefreshToken().getTokenValue());
  }

  @PostMapping("/logout")
  public BaseResponse logout() {
    authService.logout();
    return new BaseResponse(ResponseMessage.SUCCESS);
  }
}
