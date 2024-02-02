package com.pivo.weev.backend.rest.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest extends VerificationCompletionRequest {

    private String oldPassword;
    private String newPassword;
}
