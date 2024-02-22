package com.pivo.weev.backend.domain.model.messaging.template;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationTemplateModel {

    private String verificationCode;
    private String nickname;
}
