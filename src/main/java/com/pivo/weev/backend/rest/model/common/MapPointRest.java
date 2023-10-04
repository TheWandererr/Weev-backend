package com.pivo.weev.backend.rest.model.common;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.MUST_BE_NOT_NULL;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapPointRest {

    @NotNull(message = MUST_BE_NOT_NULL)
    private Double lng;
    @NotNull(message = MUST_BE_NOT_NULL)
    private Double ltd;
}
