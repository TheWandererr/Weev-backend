package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.NOTIFICATION;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.INotificationRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificationRepositoryWrapper extends GenericRepositoryWrapper<Long, NotificationJpa, INotificationRepository> {

    protected NotificationRepositoryWrapper(INotificationRepository repository) {
        super(repository, NOTIFICATION);
    }
}
