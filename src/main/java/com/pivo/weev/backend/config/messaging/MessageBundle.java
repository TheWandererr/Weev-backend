package com.pivo.weev.backend.config.messaging;

import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLocale;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageBundle {

    private final MessageSource emailMessages;
    private final MessageSource pushNotificationMessages;

    public String getEmailMessage(String code, Object... args) {
        return emailMessages.getMessage(code, args, getAcceptedLocale());
    }

    public String getPushNotificationMessage(String code, Locale locale, Object... args) {
        return pushNotificationMessages.getMessage(code, args, locale);
    }
}
