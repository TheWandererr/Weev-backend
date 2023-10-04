package com.pivo.weev.backend.rest.model.event;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.INCORRECT_RADIUS_AMOUNT;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.MUST_BE_NOT_NULL;

import com.pivo.weev.backend.rest.model.common.MapPointRest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RadiusRest {

    @NotNull(message = MUST_BE_NOT_NULL)
    private MapPointRest point;
    @NotNull(message = MUST_BE_NOT_NULL)
    @Min(value = 1, message = INCORRECT_RADIUS_AMOUNT)
    private Double value;
}
