package com.pivo.weev.backend.rest.mapping;

import com.pivo.weev.backend.domain.model.OAuthTokenDetails;
import com.pivo.weev.backend.rest.model.auth.JWTPair;
import com.pivo.weev.backend.rest.model.auth.LoginDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OAuthTokenDetailsMapper {

  @Mapping(target = "userId", source = "loginDetails.userId")
  @Mapping(target = "deviceId", source = "loginDetails.deviceId")
  @Mapping(target = "serial", source = "loginDetails.serial")
  @Mapping(target = "expiresAt", source = "jwtPair.refreshToken.expiresAt")
  OAuthTokenDetails map(LoginDetails loginDetails, JWTPair jwtPair);
}
