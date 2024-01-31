package com.pivo.weev.backend.domain.service.message;

import static com.pivo.weev.backend.utils.CollectionUtils.mapToSet;

import com.pivo.weev.backend.domain.persistance.jpa.NotificationFactory;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.DeclinationReasonJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetNotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.NotificationRepositoryWrapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepositoryWrapper notificationRepository;

    private final NotificationFactory notificationFactory;

    public void notify(MeetJpa target, UserJpa recipient, String title) {
        notify(target, recipient, title, new HashMap<>());
    }

    public void notify(MeetJpa target, UserJpa recipient, String title, Map<String, Object> details) {
        MeetNotificationJpa notification = notificationFactory.createMeetNotification(target, recipient, title, details);
        notificationRepository.save(notification);
    }

    public void notify(MeetJpa target, UserJpa recipient, String title, DeclinationReasonJpa reason) {
        NotificationJpa notification = notificationFactory.createMeetNotification(target, recipient, title, reason);
        notificationRepository.save(notification);
    }

    public void notifyAll(MeetJpa target, Collection<UserJpa> recipients, String title) {
        notifyAll(target, recipients, title, null);
    }

    public void notifyAll(MeetJpa target, Collection<UserJpa> recipients, String title, Map<String, Object> details) {
        Set<NotificationJpa> notifications = mapToSet(recipients, recipient -> notificationFactory.createMeetNotification(target, recipient, title, details));
        notificationRepository.saveAll(notifications);
    }
}
