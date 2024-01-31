package com.pivo.weev.backend.domain.service.event.model;

import com.pivo.weev.backend.domain.service.event.message.PushNotificationMessage;
import org.springframework.context.ApplicationEvent;

public class PushNotificationEvent extends ApplicationEvent {

    public PushNotificationEvent(Object source) {
        super(source);
        if (!(source instanceof PushNotificationMessage)) {
            throw new IllegalArgumentException("notification.message.expected");
        }
    }

    @Override
    public PushNotificationMessage getSource() {
        return (PushNotificationMessage) source;
    }
}
