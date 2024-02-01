package com.pivo.weev.backend.domain.persistance.jpa;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa.Type.IMPORTANT;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.DeclinationReasonJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetNotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    public MeetNotificationJpa createMeetNotification(MeetJpa meet, UserJpa recipient, String topic) {
        return createMeetNotification(meet, recipient, topic, new HashMap<>());
    }

    public MeetNotificationJpa createMeetNotification(MeetJpa meet, UserJpa recipient, String topic, Map<String, Object> details) {
        MeetNotificationJpa meetNotification = new MeetNotificationJpa();
        meetNotification.setMeet(meet);
        meetNotification.setType(IMPORTANT);
        meetNotification.setRecipient(recipient);
        meetNotification.setViewed(false);
        meetNotification.setTopic(topic);
        meetNotification.setDetails(details);
        return meetNotification;
    }


    public MeetNotificationJpa createMeetNotification(MeetJpa meet, UserJpa recipient, String topic, DeclinationReasonJpa declinationReasonJpa) {
        MeetNotificationJpa meetNotification = createMeetNotification(meet, recipient, topic);
        meetNotification.setDeclinationReasonJpa(declinationReasonJpa);
        return meetNotification;
    }
}
