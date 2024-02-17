package com.pivo.weev.backend.integration.mapping.firebase.notification;

import com.google.firebase.messaging.MulticastMessage;
import com.pivo.weev.backend.integration.firebase.model.notification.FirebasePushNotificationMessage;
import org.mapstruct.Mapper;

@Mapper
public interface MulticastMessageMapper extends NotificationMapper {

    default MulticastMessage map(FirebasePushNotificationMessage source) {
        return MulticastMessage.builder()
                               .addAllTokens(source.getRecipients())
                               .putAllData(source.getData())
                               .setNotification(mapToNotification(source))
                               .build();
    }
}
