package com.pivo.weev.backend.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse extends BaseResponse {

  private final String accessToken;
  private final String refreshToken;
}
