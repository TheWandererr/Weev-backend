package com.pivo.weev.backend.integration.firebase.client;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DatabaseClient {

    private final FirebaseDatabase api;

    public DatabaseClient(FirebaseApplication application) {
        this.api = FirebaseDatabase.getInstance(application.getInstance());
    }

    @Async(value = "commonExecutor")
    public void save(String referencePath, String childReference, String childId, Object object) {
        DatabaseReference reference = api.getReference(referencePath);
        reference.child(childReference)
                 .child(childId)
                 .setValueAsync(object);
    }
}
