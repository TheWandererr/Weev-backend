package com.pivo.weev.backend.rest.controller;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.auth.AuthTokens;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.service.auth.AuthOperationsService;
import com.pivo.weev.backend.domain.service.auth.AuthTokensService;
import com.pivo.weev.backend.rest.mapping.domain.ContactsMapper;
import com.pivo.weev.backend.rest.mapping.domain.UserSnapshotMapper;
import com.pivo.weev.backend.rest.model.request.ChangePasswordRequest;
import com.pivo.weev.backend.rest.model.request.NewPasswordRequest;
import com.pivo.weev.backend.rest.model.request.RegistrationRequest;
import com.pivo.weev.backend.rest.model.request.UsernameRequest;
import com.pivo.weev.backend.rest.model.request.VerificationRequest;
import com.pivo.weev.backend.rest.model.response.BaseResponse;
import com.pivo.weev.backend.rest.model.response.LoginResponse;
import com.pivo.weev.backend.rest.model.response.VerificationRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/registration/verification/request")
    public BaseResponse requestRegistrationVerification(@RequestBody @Valid VerificationRequest request) {
        Contacts contacts = getMapper(ContactsMapper.class).map(request);
        authOperationsService.requestRegistrationVerification(contacts);
        return new BaseResponse();
    }

    @PostMapping("/registration")
    public BaseResponse register(@RequestBody @Valid RegistrationRequest request) {
        UserSnapshot userSnapshot = getMapper(UserSnapshotMapper.class).map(request);
        authOperationsService.register(userSnapshot, request.getVerificationCode());
        return new BaseResponse();
    }

    @PostMapping("/logout")
    public BaseResponse logout(@RequestParam(required = false) Boolean allDevices) {
        authOperationsService.logout(isTrue(allDevices));
        return new BaseResponse();
    }

    @PostMapping("/password/reset/verification/request")
    public VerificationRequestResponse requestPasswordResetVerification(@RequestBody @Valid UsernameRequest request) {
        String formattedUsername = request.getUsername().toLowerCase();
        String method = authOperationsService.requestPasswordResetVerification(formattedUsername);
        return new VerificationRequestResponse(method);
    }

    @PutMapping("/password/reset")
    public BaseResponse setNewPassword(@RequestBody @Valid NewPasswordRequest request) {
        authOperationsService.setNewPassword(request.getNewPassword(), request.getUsername().toLowerCase(), request.getVerificationCode());
        return new BaseResponse();
    }

    @PostMapping("/password/change/verification/request")
    public BaseResponse requestChangePasswordVerification(@RequestBody @Valid VerificationRequest request) {
        Contacts contacts = getMapper(ContactsMapper.class).map(request);
        String method = authOperationsService.requestChangePasswordVerification(contacts);
        return new VerificationRequestResponse(method);
    }

    @PutMapping("/password/change")
    public BaseResponse changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        authOperationsService.changePassword(request.getOldPassword(), request.getNewPassword(), request.getVerificationCode());
        return new BaseResponse();
    }
}
