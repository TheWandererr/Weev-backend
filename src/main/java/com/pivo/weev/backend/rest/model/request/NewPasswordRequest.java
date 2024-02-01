package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NewPasswordRequest extends VerificationCompletionRequest {

    @NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
    private String newPassword;
    @NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
    private String username;
}


