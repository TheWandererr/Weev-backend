package com.pivo.weev.backend.rest.model.event;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_AMOUNT_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_NULL_ERROR;

import com.pivo.weev.backend.rest.model.common.MapPointRest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RadiusRest {

    @NotNull(message = MUST_BE_NOT_NULL_ERROR)
    private MapPointRest point;
    @NotNull(message = MUST_BE_NOT_NULL_ERROR)
    @Min(value = 0, message = INVALID_AMOUNT_ERROR)
    private Double value;
}
