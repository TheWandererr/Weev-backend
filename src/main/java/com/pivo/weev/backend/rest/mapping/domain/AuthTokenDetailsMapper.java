package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.auth.AuthTokenDetails;
import com.pivo.weev.backend.domain.model.auth.AuthTokens;
import com.pivo.weev.backend.domain.model.auth.LoginDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AuthTokenDetailsMapper {

    @Mapping(target = "userId", source = "loginDetails.userId")
    @Mapping(target = "deviceId", source = "loginDetails.deviceId")
    @Mapping(target = "serial", source = "loginDetails.serial")
    @Mapping(target = "expiresAt", source = "authTokens.refreshToken.expiresAt")
    AuthTokenDetails map(LoginDetails loginDetails, AuthTokens authTokens);
}
