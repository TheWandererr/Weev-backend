package com.pivo.weev.backend.rest.model.meet;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_AMOUNT_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryFeeRest {

    @DecimalMin(value = "0.01", message = INVALID_AMOUNT_ERROR)
    private BigDecimal amount;
    @NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
    private String currency;
}
