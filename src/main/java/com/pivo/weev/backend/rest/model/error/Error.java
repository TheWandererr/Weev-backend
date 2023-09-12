package com.pivo.weev.backend.rest.model.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Error {

  private final String errorCode;
  private final String messageCode;

}
