package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public record NewPasswordRequest(@NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
                                 @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
                                 String newPassword,
                                 @NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
                                 String username) {

}


