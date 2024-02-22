package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.template.EmailVerificationTemplateModel;
import com.pivo.weev.backend.domain.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EmailVerificationTemplateModelMapper {

    @Mapping(target = "verificationCode", source = "verificationCode")
    @Mapping(target = "nickname", source = "user.nickname")
    EmailVerificationTemplateModel map(User user, String verificationCode);
}
