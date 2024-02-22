package com.pivo.weev.backend.integration.firebase.service;

import com.pivo.weev.backend.integration.firebase.client.MessagingClient;
import com.pivo.weev.backend.integration.firebase.model.notification.FirebasePushNotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service()
@RequiredArgsConstructor
public class FirebasePushNotificationService {

    private final MessagingClient client;

    public void send(FirebasePushNotificationMessage message) {
        client.send(message);
    }
}
