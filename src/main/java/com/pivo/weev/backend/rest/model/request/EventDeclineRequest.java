package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.INVALID_DECLINATION_REASON;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDeclineRequest {

    @NotBlank(message = INVALID_DECLINATION_REASON)
    private String declinationReason;
}
