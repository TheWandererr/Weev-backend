package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.user.UserSnapshotRest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse extends BaseResponse {

    private final String accessToken;
    private final String refreshToken;
    private UserSnapshotRest user;

    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
