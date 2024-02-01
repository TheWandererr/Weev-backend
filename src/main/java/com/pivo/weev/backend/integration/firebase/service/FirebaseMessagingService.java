package com.pivo.weev.backend.integration.firebase.service;

import com.pivo.weev.backend.integration.firebase.client.MessagingClient;
import com.pivo.weev.backend.integration.firebase.model.PushNotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {

    private final MessagingClient client;

    public void send(PushNotificationMessage message) {
        client.send(message);
    }
}
