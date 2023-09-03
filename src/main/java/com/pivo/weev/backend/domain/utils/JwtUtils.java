package com.pivo.weev.backend.domain.utils;

import com.pivo.weev.backend.web.utils.Constants.Claims;
import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.jwt.Jwt;

@UtilityClass
public final class JwtUtils {

  public static String getDeviceId(Jwt jwt) {
    return jwt.getClaim(Claims.DEVICE_ID);
  }

  public static Long getUserId(Jwt jwt) {
    return jwt.getClaim(Claims.USER_ID);
  }

  public static String getSerial(Jwt jwt) {
    return jwt.getClaim(Claims.SERIAL);
  }
}
