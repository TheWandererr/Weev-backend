package com.pivo.weev.backend.integration.mapping.firebase;

import com.google.firebase.messaging.MulticastMessage;
import com.pivo.weev.backend.integration.firebase.model.notification.PushNotificationMessage;
import org.mapstruct.Mapper;

@Mapper
public interface MulticastMessageMapper extends NotificationMapper {

    default MulticastMessage map(PushNotificationMessage source) {
        return MulticastMessage.builder()
                               .addAllTokens(source.getRecipients())
                               .putAllData(source.getData())
                               .setNotification(mapToNotification(source))
                               .build();
    }
}
