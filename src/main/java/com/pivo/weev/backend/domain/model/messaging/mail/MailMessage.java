package com.pivo.weev.backend.domain.model.messaging.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailMessage {

    private String subject;
    private String content;
    private String recipient;
}
