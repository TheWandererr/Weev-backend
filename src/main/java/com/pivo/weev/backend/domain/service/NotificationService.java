package com.pivo.weev.backend.domain.service;

import static com.pivo.weev.backend.common.utils.CollectionUtils.mapToSet;

import com.pivo.weev.backend.domain.persistance.jpa.NotificationFactory;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventNotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.NotificationRepositoryWrapper;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepositoryWrapper notificationRepository;

    private final NotificationFactory notificationFactory;

    public void notify(EventJpa target, UserJpa recipient, String title) {
        EventNotificationJpa notification = notificationFactory.createEventNotification(target, recipient, title);
        notificationRepository.save(notification);
    }

    public void notify(EventJpa target, UserJpa recipient, String title, DeclinationReason reason) {
        NotificationJpa notification = notificationFactory.createEventNotification(target, recipient, title, reason);
        notificationRepository.save(notification);
    }

    public void notifyAll(Set<UserJpa> recipients, EventJpa target, String title) {
        Set<NotificationJpa> notifications = mapToSet(recipients, recipient -> notificationFactory.createEventNotification(target, recipient, title));
        notificationRepository.saveAll(notifications);
    }
}
