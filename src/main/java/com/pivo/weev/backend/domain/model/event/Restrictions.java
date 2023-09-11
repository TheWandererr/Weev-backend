package com.pivo.weev.backend.domain.model.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Restrictions {

  private boolean joinAfterStartDisallowed;
  private boolean joinByRequest;
}
