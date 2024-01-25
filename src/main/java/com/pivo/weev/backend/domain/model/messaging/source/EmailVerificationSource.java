package com.pivo.weev.backend.domain.model.messaging.source;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmailVerificationSource {

    private final String verificationCode;
}
