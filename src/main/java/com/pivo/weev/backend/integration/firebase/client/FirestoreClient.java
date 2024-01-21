package com.pivo.weev.backend.integration.firebase.client;

import static com.google.firebase.cloud.FirestoreClient.getFirestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Component;

@Component
public class FirestoreClient {

    private final Firestore api;

    public FirestoreClient(FirebaseApplication application) {
        this.api = getFirestore(application.getInstance());
    }

    public DocumentSnapshot getSnapshot(String collection, String id) {
        DocumentReference documentReference = api.collection(collection).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        try {
            return future.get();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException executionException) {
            return null;
        }
    }
}
