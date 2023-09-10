package com.pivo.weev.backend.rest.model.event;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryFeeRest {

  @DecimalMin(value = "0.01", message = "incorrect.fee.amount")
  private BigDecimal fee;
  private String currency;
}
