package com.pivo.weev.backend.domain.model.event;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryFee {

  private BigDecimal amount;
  private String currency;
}
