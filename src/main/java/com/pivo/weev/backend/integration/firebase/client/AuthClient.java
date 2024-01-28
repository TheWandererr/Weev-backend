package com.pivo.weev.backend.integration.firebase.client;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

import com.google.firebase.auth.FirebaseAuth;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthClient {

    private final FirebaseAuth api;

    @Autowired
    public AuthClient(FirebaseApplication application) {
        this.api = getInstance(application.getInstance());
    }
}
