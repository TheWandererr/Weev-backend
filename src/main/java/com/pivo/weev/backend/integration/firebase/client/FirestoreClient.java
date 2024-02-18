package com.pivo.weev.backend.integration.firebase.client;

import static com.google.cloud.firestore.FieldPath.documentId;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.Firestore;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FirestoreClient {

    private final Firestore api;
    private final ObjectMapper objectMapper;

    public FirestoreClient(FirebaseApplication application, ObjectMapper objectMapper) {
        this.api = com.google.firebase.cloud.FirestoreClient.getFirestore(application.getInstance());
        this.objectMapper = objectMapper;
    }

    public void save(String collection, String reference, Object object) {
        api.collection(collection)
           .document(reference)
           .set(objectMapper.convertValue(object, new TypeReference<>() {
           }));
    }

    public void update(String collection, String reference, Map<String, Object> updates) {
        api.collection(collection)
           .document(reference)
           .update(updates);
    }

    public <T> T find(String collection, String reference, Class<T> type) {
        CompletableFuture<T> future = new CompletableFuture<>();
        api.collection(collection)
           .document(reference)
           .addSnapshotListener((documentSnapshot, exception) -> {
               if (nonNull(exception)) {
                   log.error(exception.getMessage());
                   future.completeExceptionally(exception);
                   return;
               }
               if (nonNull(documentSnapshot)) {
                   Map<String, Object> data = documentSnapshot.getData();
                   try {
                       future.complete(objectMapper.convertValue(data, type));
                   } catch (Exception mappingException) {
                       log.error(mappingException.getMessage());
                       future.completeExceptionally(mappingException);
                   }
               }
           });
        return future.join();
    }

    public <T> List<T> findAll(String collection, List<String> references, Class<T> type) {
        CompletableFuture<List<T>> future = new CompletableFuture<>();
        api.collection(collection)
           .whereIn(documentId(), references)
           .addSnapshotListener((snapshot, exception) -> {
               if (nonNull(exception)) {
                   log.error(exception.getMessage());
                   future.completeExceptionally(exception);
                   return;
               }
               if (nonNull(snapshot)) {
                   try {
                       List<T> result = mapToList(snapshot.getDocuments(), document -> objectMapper.convertValue(document.getData(), type));
                       future.complete(result);
                   } catch (Exception mappingException) {
                       log.error(mappingException.getMessage());
                       future.completeExceptionally(mappingException);
                   }
               }
           });
        return future.join();
    }
}
