package com.pivo.weev.backend.domain.service;

import com.pivo.weev.backend.domain.persistance.jpa.NotificationFactory;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventNotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.NotificationRepositoryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepositoryWrapper notificationRepository;

    private final NotificationFactory notificationFactory;

    public void createNotification(EventJpa target, String title) {
        EventNotificationJpa notification = notificationFactory.createEventNotification(target, title);
        notificationRepository.save(notification);
    }

    public void createNotification(EventJpa target, String title, DeclinationReason reason) {
        NotificationJpa notification = notificationFactory.createEventNotification(target, title, reason);
        notificationRepository.save(notification);
    }
}
