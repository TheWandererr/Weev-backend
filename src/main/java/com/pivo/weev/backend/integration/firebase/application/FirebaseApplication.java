package com.pivo.weev.backend.integration.firebase.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.pivo.weev.backend.integration.firebase.FirebaseProperties;
import com.pivo.weev.backend.utils.IOUtils;
import java.io.IOException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FirebaseApplication {

    private final FirebaseApp instance;

    @Autowired
    public FirebaseApplication(FirebaseProperties properties, ObjectMapper mapper) throws IOException {
        String credentials = mapper.writeValueAsString(properties.getCredentials());
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(IOUtils.getInputStream(credentials));
        var options = FirebaseOptions.builder()
                                     .setReadTimeout(properties.getReadTimeout())
                                     .setConnectTimeout(properties.getConnectTimeout())
                                     .setCredentials(googleCredentials)
                                     .setStorageBucket(properties.getStorageBucket())
                                     .build();
        this.instance = FirebaseApp.initializeApp(options);
    }
}
