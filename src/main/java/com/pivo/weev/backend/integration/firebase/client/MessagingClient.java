package com.pivo.weev.backend.integration.firebase.client;

import com.google.firebase.messaging.FirebaseMessaging;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagingClient {

    private final FirebaseMessaging api;

    @Autowired
    public MessagingClient(FirebaseApplication application) {
        this.api = FirebaseMessaging.getInstance(application.getInstance());
    }
}
