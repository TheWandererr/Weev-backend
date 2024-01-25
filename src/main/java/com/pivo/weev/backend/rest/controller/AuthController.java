package com.pivo.weev.backend.rest.controller;

import static com.pivo.weev.backend.domain.model.auth.VerificationScope.REGISTRATION;
import static com.pivo.weev.backend.rest.model.response.BaseResponse.ResponseMessage.SUCCESS;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.auth.AuthTokens;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.service.auth.AuthOperationsService;
import com.pivo.weev.backend.domain.service.auth.AuthTokensService;
import com.pivo.weev.backend.rest.mapping.domain.UserSnapshotMapper;
import com.pivo.weev.backend.rest.model.request.EmailRequest;
import com.pivo.weev.backend.rest.model.request.RegistrationRequest;
import com.pivo.weev.backend.rest.model.request.VerificationCompletionRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokensService authTokensService;
    private final AuthOperationsService authOperationsService;

    @GetMapping("/tokens/refresh")
    @PreAuthorize("hasAnyAuthority('SCOPE_refresh')")
    public LoginResponse refreshTokens() {
        AuthTokens authTokens = authTokensService.refreshTokens();
        return new LoginResponse(authTokens.getAccessTokenValue(), authTokens.getRefreshTokenValue());
    }

    @PostMapping("/registration/email/verification/request")
    public BaseResponse requestEmailVerification(@RequestBody @Valid EmailRequest request) {
        authOperationsService.requestEmailVerification(request.getEmail(), REGISTRATION);
        return new BaseResponse(SUCCESS);
    }

    @PutMapping("/registration/email/verification/completion")
    public BaseResponse completeEmailVerification(@RequestBody @Valid VerificationCompletionRequest request) {
        authOperationsService.completeEmailVerification(request.getCode(), request.getEmail());
        return new BaseResponse(SUCCESS);
    }

    @PostMapping("/registration")
    public BaseResponse register(@RequestBody @Valid RegistrationRequest request) {
        UserSnapshot userSnapshot = getMapper(UserSnapshotMapper.class).map(request);
        authOperationsService.register(userSnapshot);
        return new BaseResponse(SUCCESS);
    }

    @PostMapping("/logout")
    public BaseResponse logout() {
        authOperationsService.logout();
        return new BaseResponse(SUCCESS);
    }
}
