package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.mapping.domain.decorator.NotificationMapperDecorator;
import com.pivo.weev.backend.domain.model.user.Notification;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import java.util.List;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper
@DecoratedWith(NotificationMapperDecorator.class)
public interface NotificationMapper {

    Notification map(NotificationJpa notificationJpa);

    List<Notification> map(List<NotificationJpa> notificationJpa);
}
