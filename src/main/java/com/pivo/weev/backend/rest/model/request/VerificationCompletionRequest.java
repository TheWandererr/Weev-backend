package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationCompletionRequest {

    @NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
    private String verificationCode;
}
