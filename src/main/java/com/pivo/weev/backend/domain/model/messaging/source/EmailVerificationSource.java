package com.pivo.weev.backend.domain.model.messaging.source;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationSource {

    private String verificationCode;
    private String nickname;
}
