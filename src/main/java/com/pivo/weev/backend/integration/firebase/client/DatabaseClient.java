package com.pivo.weev.backend.integration.firebase.client;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DatabaseClient {

    private final FirebaseDatabase api;

    public DatabaseClient(FirebaseApplication application) {
        this.api = FirebaseDatabase.getInstance(application.getInstance());
    }

    @Async(value = "commonExecutor")
    public void save(String referencePath, String childReference, Long childId, Object object) {
        DatabaseReference reference = api.getReference(referencePath);
        reference.child(childReference)
                 .child(childId.toString())
                 .setValueAsync(object);
    }

    @Async(value = "commonExecutor")
    public void update(String referencePath, String childReference, Long childId, Map<String, Object> updates) {
        DatabaseReference reference = api.getReference(referencePath);
        reference.child(childReference)
                 .child(childId.toString())
                 .updateChildrenAsync(updates);
    }

    public <T> T find(String referencePath, String childReference, Long childId, Class<T> type) {
        CompletableFuture<T> future = new CompletableFuture<>();
        DatabaseReference reference = api.getReference(referencePath);
        DatabaseReference child = reference.child(childReference).child(childId.toString());
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    T value = snapshot.getValue(type);
                    future.complete(value);
                } catch (Exception exception) {
                    future.completeExceptionally(exception);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future.join();
    }
}
