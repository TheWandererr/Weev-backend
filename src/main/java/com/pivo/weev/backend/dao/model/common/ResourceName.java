package com.pivo.weev.backend.dao.model.common;

import com.pivo.weev.backend.common.utils.Enumerated;

public enum ResourceName implements Enumerated {

  USER,
  OAUTH_TOKEN_DETAILS;

  @Override
  public String getName() {
    return name();
  }
}
