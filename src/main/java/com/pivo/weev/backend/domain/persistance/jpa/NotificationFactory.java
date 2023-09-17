package com.pivo.weev.backend.domain.persistance.jpa;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa.Type.IMPORTANT;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    public NotificationJpa createEventNotification(EventJpa event, String title) {
        NotificationJpa eventNotificationJpa = new NotificationJpa();
        eventNotificationJpa.setEvent(event);
        eventNotificationJpa.setType(IMPORTANT);
        eventNotificationJpa.setRecipient(event.getCreator());
        eventNotificationJpa.setViewed(false);
        eventNotificationJpa.setTitle(title);
        return eventNotificationJpa;
    }

    public NotificationJpa createEventNotification(EventJpa event, String title, DeclinationReason declinationReason) {
        NotificationJpa eventNotification = createEventNotification(event, title);
        eventNotification.setDeclinationReason(declinationReason);
        return eventNotification;
    }
}
