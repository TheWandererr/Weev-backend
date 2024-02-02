package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.source.EmailVerificationSource;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EmailVerificationSourceMapper {

    @Mapping(target = "verificationCode", source = "verificationCode")
    @Mapping(target = "nickname", source = "user.nickname")
    EmailVerificationSource map(UserJpa user, String verificationCode);
}
