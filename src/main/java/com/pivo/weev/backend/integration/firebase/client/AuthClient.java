package com.pivo.weev.backend.integration.firebase.client;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthClient {

    private final FirebaseAuth api;

    @Autowired
    public AuthClient(FirebaseApplication application) {
        this.api = FirebaseAuth.getInstance(application.getInstance());
    }

    public String generateEmailVerificationLink(String email, String url) throws FirebaseAuthException {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                                                                  .setUrl(url)
                                                                  .setHandleCodeInApp(true)
                                                                  .build();
        return api.generateEmailVerificationLink(email, actionCodeSettings);
    }
}
