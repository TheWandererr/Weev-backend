package com.pivo.weev.backend.domain.persistance.jpa;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa.Type.IMPORTANT;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReasonJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventNotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    public EventNotificationJpa createEventNotification(EventJpa event, UserJpa recipient, String title) {
        return createEventNotification(event, recipient, title, new HashMap<>());
    }

    public EventNotificationJpa createEventNotification(EventJpa event, UserJpa recipient, String title, Map<String, Object> details) {
        EventNotificationJpa eventNotificationJpa = new EventNotificationJpa();
        eventNotificationJpa.setEvent(event);
        eventNotificationJpa.setType(IMPORTANT);
        eventNotificationJpa.setRecipient(recipient);
        eventNotificationJpa.setViewed(false);
        eventNotificationJpa.setTitle(title);
        eventNotificationJpa.setDetails(details);
        return eventNotificationJpa;
    }


    public EventNotificationJpa createEventNotification(EventJpa event, UserJpa recipient, String title, DeclinationReasonJpa declinationReasonJpa) {
        EventNotificationJpa eventNotification = createEventNotification(event, recipient, title);
        eventNotification.setDeclinationReasonJpa(declinationReasonJpa);
        return eventNotification;
    }
}
