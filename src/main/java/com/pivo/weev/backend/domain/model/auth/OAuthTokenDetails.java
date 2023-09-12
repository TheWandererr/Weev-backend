package com.pivo.weev.backend.domain.model.auth;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthTokenDetails {

  private Long userId;
  private String deviceId;
  private String serial;
  private Instant expiresAt;

}
