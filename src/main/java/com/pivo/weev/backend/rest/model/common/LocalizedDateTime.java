package com.pivo.weev.backend.rest.model.common;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalizedDateTime {

  private LocalDateTime localDateTime;
  private String localTimeZoneId;
}
