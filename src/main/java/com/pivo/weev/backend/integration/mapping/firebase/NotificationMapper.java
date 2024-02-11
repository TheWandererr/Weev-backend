package com.pivo.weev.backend.integration.mapping.firebase;

import com.google.firebase.messaging.Notification;
import com.pivo.weev.backend.integration.firebase.model.notification.PushNotificationMessage;
import org.mapstruct.Mapper;

@Mapper
public interface NotificationMapper {

    default Notification mapToNotification(PushNotificationMessage pushNotificationMessage) {
        return Notification
                .builder()
                .setTitle(pushNotificationMessage.getTitle())
                .setBody(pushNotificationMessage.getBody())
                .build();
    }
}
