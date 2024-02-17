package com.pivo.weev.backend.integration.mapping.firebase.notification;

import com.google.firebase.messaging.Notification;
import com.pivo.weev.backend.integration.firebase.model.notification.FirebasePushNotificationMessage;
import org.mapstruct.Mapper;

@Mapper
public interface NotificationMapper {

    default Notification mapToNotification(FirebasePushNotificationMessage firebasePushNotificationMessage) {
        return Notification
                .builder()
                .setTitle(firebasePushNotificationMessage.getTitle())
                .setBody(firebasePushNotificationMessage.getBody())
                .build();
    }
}
