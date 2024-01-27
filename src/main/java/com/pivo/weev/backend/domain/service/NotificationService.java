package com.pivo.weev.backend.domain.service;

import static com.pivo.weev.backend.utils.CollectionUtils.mapToSet;

import com.pivo.weev.backend.domain.persistance.jpa.NotificationFactory;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReasonJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventNotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.NotificationRepositoryWrapper;
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

    public void notify(EventJpa target, UserJpa recipient, String title) {
        notify(target, recipient, title, new HashMap<>());
    }

    public void notify(EventJpa target, UserJpa recipient, String title, Map<String, Object> details) {
        EventNotificationJpa notification = notificationFactory.createEventNotification(target, recipient, title, details);
        notificationRepository.save(notification);
    }

    public void notify(EventJpa target, UserJpa recipient, String title, DeclinationReasonJpa reason) {
        NotificationJpa notification = notificationFactory.createEventNotification(target, recipient, title, reason);
        notificationRepository.save(notification);
    }

    public void notifyAll(EventJpa target, Set<UserJpa> recipients, String title) {
        Set<NotificationJpa> notifications = mapToSet(recipients, recipient -> notificationFactory.createEventNotification(target, recipient, title));
        notificationRepository.saveAll(notifications);
    }
}
