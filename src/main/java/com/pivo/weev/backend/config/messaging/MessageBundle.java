package com.pivo.weev.backend.config.messaging;

import static com.pivo.weev.backend.rest.utils.LocaleUtils.getAcceptedLocale;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageBundle {

    private final MessageSource emailMessages;

    public String getEmailMessage(String code, Object... args) {
        return emailMessages.getMessage(code, args, getAcceptedLocale());
    }
}
