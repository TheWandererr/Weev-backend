package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.source.EmailVerificationSource;
import com.pivo.weev.backend.domain.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EmailVerificationSourceMapper {

    @Mapping(target = "verificationCode", source = "verificationCode")
    @Mapping(target = "nickname", source = "user.nickname")
    EmailVerificationSource map(User user, String verificationCode);
}
