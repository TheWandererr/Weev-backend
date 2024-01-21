package com.pivo.weev.backend.integration.firebase.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.pivo.weev.backend.integration.firebase.FirebaseProperties;
import com.pivo.weev.backend.utils.IOUtils;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FirebaseApplication {

    private final FirebaseApp application;
    private final FirebaseOptions options;

    @Autowired
    public FirebaseApplication(FirebaseProperties properties, ObjectMapper mapper) throws IOException {
        String credentials = mapper.writeValueAsString(properties.getCredentials());
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(IOUtils.getInputStream(credentials));
        this.options = FirebaseOptions.builder()
                                      .setReadTimeout(properties.getReadTimeout())
                                      .setConnectTimeout(properties.getConnectTimeout())
                                      .setCredentials(googleCredentials)
                                      .setStorageBucket("weev-e2813.appspot.com") // TODO
                                      .build();
        this.application = FirebaseApp.initializeApp(options);
    }

    public FirebaseApp getInstance() {
        return application;
    }
}
