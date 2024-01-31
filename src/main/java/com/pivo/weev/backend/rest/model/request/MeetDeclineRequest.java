package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.DECLINATION_REASON_UNSUPPORTED_ERROR;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetDeclineRequest {

    @NotBlank(message = DECLINATION_REASON_UNSUPPORTED_ERROR)
    private String declinationReason;
}
