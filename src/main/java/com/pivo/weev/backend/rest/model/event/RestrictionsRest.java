package com.pivo.weev.backend.rest.model.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestrictionsRest {

  private Boolean joinAfterStartDisallowed;
  private Boolean joinByRequest;
}
