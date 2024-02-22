package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.user.Notification;
import com.pivo.weev.backend.rest.model.user.NotificationRest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NotificationRestMapper {

    @Mapping(target = "details", ignore = true)
    NotificationRest map(Notification source);

    List<NotificationRest> map(List<Notification> source);
}
