package com.pivo.weev.backend.rest.model.event;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_AMOUNT;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryFeeRest {

    @DecimalMin(value = "0.01", message = INVALID_AMOUNT)
    private BigDecimal amount;
    @NotBlank(message = MUST_BE_NOT_BLANK)
    private String currency;
}
