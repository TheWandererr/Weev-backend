package com.pivo.weev.backend.rest.model.event;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.INCORRECT_FEE_AMOUNT;
import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryFeeRest {

  @DecimalMin(value = "0.01", message = INCORRECT_FEE_AMOUNT)
  private BigDecimal fee;
  @NotBlank(message = MUST_BE_NOT_BLANK)
  private String currency;
}
