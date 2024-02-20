package com.pivo.weev.backend.integration.firebase.client;

import static com.google.cloud.firestore.Filter.inArray;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.ID;
import static com.pivo.weev.backend.utils.CollectionUtils.first;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FirestoreClient {

    private final Firestore api;
    private final ObjectMapper objectMapper;
    private final ThreadPoolTaskExecutor firebaseFirestoreExecutor;

    public FirestoreClient(FirebaseApplication application, ObjectMapper objectMapper, ThreadPoolTaskExecutor firebaseFirestoreExecutor) {
        this.api = com.google.firebase.cloud.FirestoreClient.getFirestore(application.getInstance());
        this.objectMapper = objectMapper;
        this.firebaseFirestoreExecutor = firebaseFirestoreExecutor;
    }

    public void save(String collection, String reference, Object document) {
        Map<String, Object> fields = objectMapper.convertValue(document, new TypeReference<>() {
        });
        api.collection(collection)
           .document(reference)
           .set(fields);
    }

    public void save(String collection, String parentReference, String childCollection, Object childDocument) {
        Map<String, Object> fields = objectMapper.convertValue(childDocument, new TypeReference<>() {
        });
        api.collection(collection)
           .document(parentReference)
           .collection(childCollection)
           .add(fields);
    }

    public void save(String collection, String parentReference, String childCollection, Object childDocument, Runnable runnable) {
        Map<String, Object> fields = objectMapper.convertValue(childDocument, new TypeReference<>() {
        });
        api.collection(collection)
           .document(parentReference)
           .collection(childCollection)
           .add(fields)
           .addListener(runnable, firebaseFirestoreExecutor);
    }

    public void update(String collection, String reference, Map<String, Object> updates) {
        api.collection(collection)
           .document(reference)
           .update(updates);
    }

    public <T> CompletableFuture<T> find(String collection, String reference, Class<T> type) {
        CompletableFuture<T> future = new CompletableFuture<>();
        EventListener<DocumentSnapshot> snapshotListener = createSnapshotListener(type, future);
        api.collection(collection)
           .document(reference)
           .addSnapshotListener(snapshotListener);
        return future;
    }

    public <T> CompletableFuture<T> find(String collection, String reference, String childCollection, String orderBy, Class<T> type) {
        CompletableFuture<T> future = new CompletableFuture<>();
        EventListener<QuerySnapshot> snapshotListener = (querySnapshot, exception) -> {
            if (checkError(exception)) {
                future.completeExceptionally(exception);
                return;
            }
            if (isNull(querySnapshot)) {
                future.complete(null);
                return;
            }
            QueryDocumentSnapshot document = first(querySnapshot.getDocuments());
            if (isNull(document)) {
                future.complete(null);
                return;
            }
            future.complete(objectMapper.convertValue(document.getData(), type));
        };
        api.collection(collection)
           .document(reference)
           .collection(childCollection)
           .orderBy(orderBy)
           .limit(1)
           .addSnapshotListener(snapshotListener);
        return future;
    }

    private <T> EventListener<DocumentSnapshot> createSnapshotListener(Class<T> type, CompletableFuture<T> future) {
        return (documentSnapshot, exception) -> {
            if (checkError(exception)) {
                future.completeExceptionally(exception);
                return;
            }
            if (isNull(documentSnapshot)) {
                future.complete(null);
                return;
            }
            Map<String, Object> data = documentSnapshot.getData();
            if (isNull(data)) {
                future.complete(null);
                return;
            }
            try {
                T object = objectMapper.convertValue(data, type);
                future.complete(object);
            } catch (Exception mappingException) {
                log.error(mappingException.getMessage());
                future.completeExceptionally(mappingException);
            }
        };
    }

    public <T> CompletableFuture<List<T>> findAll(String collection, List<String> references, String orderBy, Class<T> type) {
        CompletableFuture<List<T>> future = new CompletableFuture<>();
        EventListener<QuerySnapshot> snapshotsListener = createSnapshotsListener(type, future);
        api.collection(collection)
           .where(inArray(ID, references))
           .orderBy(orderBy)
           .addSnapshotListener(snapshotsListener);
        return future;
    }

    public <T> CompletableFuture<List<T>> findAll(String collection, String reference, String childCollection, String orderBy, Integer offset, Integer limit, Class<T> type) {
        CompletableFuture<List<T>> future = new CompletableFuture<>();
        EventListener<QuerySnapshot> snapshotListener = createSnapshotsListener(type, future);
        api.collection(collection)
           .document(reference)
           .collection(childCollection)
           .orderBy(orderBy)
           .offset(offset)
           .limit(limit)
           .addSnapshotListener(snapshotListener);
        return future;
    }

    private <T> EventListener<QuerySnapshot> createSnapshotsListener(Class<T> type, CompletableFuture<List<T>> future) {
        return (snapshot, exception) -> {
            if (checkError(exception)) {
                future.completeExceptionally(exception);
                return;
            }
            if (isNull(snapshot)) {
                future.complete(emptyList());
                return;
            }
            try {
                List<T> result = mapToList(snapshot.getDocuments(), document -> objectMapper.convertValue(document.getData(), type));
                future.complete(result);
            } catch (Exception mappingException) {
                log.error(mappingException.getMessage());
                future.completeExceptionally(mappingException);
            }
        };
    }

    private boolean checkError(Exception exception) {
        if (nonNull(exception)) {
            log.error(exception.getMessage());
            return true;
        }
        return false;
    }
}
