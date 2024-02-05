package com.pivo.weev.backend.domain.service.event;

import static java.util.Collections.singleton;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MeetMapper;
import com.pivo.weev.backend.domain.mapping.domain.UserMapper;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent.PushNotificationModel;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventFactory {

    public PushNotificationEvent buildNotificationEvent(MeetJpa meetJpa, Set<UserJpa> recipientsJpa, String topic, Map<String, Object> details) {
        Meet meet = getMapper(MeetMapper.class).map(meetJpa);
        Set<User> recipients = getMapper(UserMapper.class).map(recipientsJpa);
        PushNotificationModel pushNotificationModel = new PushNotificationModel(meet, recipients, topic, details);
        return new PushNotificationEvent(pushNotificationModel);
    }

    public PushNotificationEvent buildNotificationEvent(MeetJpa meetJpa, Set<UserJpa> recipientsJpa, String topic) {
        return buildNotificationEvent(meetJpa, recipientsJpa, topic, null);
    }

    public PushNotificationEvent buildNotificationEvent(MeetJpa meetJpa, UserJpa recipientJpa, String topic, Map<String, Object> details) {
        return buildNotificationEvent(meetJpa, singleton(recipientJpa), topic, details);
    }

    public PushNotificationEvent buildNotificationEvent(MeetJpa meetJpa, UserJpa recipientJpa, String topic) {
        return buildNotificationEvent(meetJpa, singleton(recipientJpa), topic, null);
    }
}
