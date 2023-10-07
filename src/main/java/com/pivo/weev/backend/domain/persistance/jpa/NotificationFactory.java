package com.pivo.weev.backend.domain.persistance.jpa;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa.Type.IMPORTANT;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventNotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    public EventNotificationJpa createEventNotification(EventJpa event, UserJpa recipient, String title) {
        EventNotificationJpa eventNotificationJpa = new EventNotificationJpa();
        eventNotificationJpa.setEvent(event);
        eventNotificationJpa.setType(IMPORTANT);
        eventNotificationJpa.setRecipient(recipient);
        eventNotificationJpa.setViewed(false);
        eventNotificationJpa.setTitle(title);
        return eventNotificationJpa;
    }

    public EventNotificationJpa createEventNotification(EventJpa event, UserJpa recipient, String title, DeclinationReason declinationReason) {
        EventNotificationJpa eventNotification = createEventNotification(event, recipient, title);
        eventNotification.setDeclinationReason(declinationReason);
        return eventNotification;
    }
}
