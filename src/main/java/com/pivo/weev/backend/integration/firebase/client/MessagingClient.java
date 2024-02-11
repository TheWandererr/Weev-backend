package com.pivo.weev.backend.integration.firebase.client;

import static org.mapstruct.factory.Mappers.getMapper;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import com.pivo.weev.backend.integration.firebase.model.notification.PushNotificationMessage;
import com.pivo.weev.backend.integration.mapping.firebase.MessageMapper;
import com.pivo.weev.backend.integration.mapping.firebase.MulticastMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MessagingClient {

    private final FirebaseMessaging api;

    @Autowired
    public MessagingClient(FirebaseApplication application) {
        this.api = FirebaseMessaging.getInstance(application.getInstance());
    }

    @Async(value = "pushNotificationsExecutor")
    public void send(PushNotificationMessage pushNotificationMessage) {
        if (pushNotificationMessage.isMulticast()) {
            MulticastMessage multicastMessage = getMapper(MulticastMessageMapper.class).map(pushNotificationMessage);
            api.sendEachForMulticastAsync(multicastMessage);
        } else {
            Message message = getMapper(MessageMapper.class).map(pushNotificationMessage);
            api.sendAsync(message);
        }
    }
}
