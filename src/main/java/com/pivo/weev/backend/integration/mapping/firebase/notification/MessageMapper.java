package com.pivo.weev.backend.integration.mapping.firebase.notification;

import static com.pivo.weev.backend.utils.CollectionUtils.first;

import com.google.firebase.messaging.Message;
import com.pivo.weev.backend.integration.firebase.model.notification.PushNotificationMessage;
import org.mapstruct.Mapper;

@Mapper
public interface MessageMapper extends NotificationMapper {

    default Message map(PushNotificationMessage source) {
        return Message.builder()
                      .setNotification(mapToNotification(source))
                      .setToken(first(source.getRecipients()))
                      .putAllData(source.getData())
                      .build();

    }
}
