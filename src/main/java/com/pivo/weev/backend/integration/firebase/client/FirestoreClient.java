package com.pivo.weev.backend.integration.firebase.client;

import static com.google.cloud.firestore.FieldPath.documentId;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import java.util.ArrayList;
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
        EventListener<DocumentSnapshot> snapshotListener = createSnapshotListener(future, type);
        api.collection(collection)
           .document(reference)
           .addSnapshotListener(snapshotListener);
        return future.join();
    }

    private <T> EventListener<DocumentSnapshot> createSnapshotListener(CompletableFuture<T> future, Class<T> type) {
        return (documentSnapshot, exception) -> {
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
        };
    }

    public <T> List<T> findAll(String collection, List<String> references, Class<T> type) {
        CompletableFuture<List<T>> future = new CompletableFuture<>();
        EventListener<QuerySnapshot> snapshotsListener = createSnapshotsListener(future, type);
        api.collection(collection)
           .whereIn(documentId(), references)
           .addSnapshotListener(snapshotsListener);
        return future.join();
    }

    private <T> EventListener<QuerySnapshot> createSnapshotsListener(CompletableFuture<List<T>> future, Class<T> type) {
        return (snapshot, exception) -> {
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
        };
    }

    public <T> List<T> findAllChildrenPageable(String collection,
                                               String reference,
                                               String childField,
                                               Integer offset,
                                               Integer limit,
                                               Class<T> type) {
        CompletableFuture<List<T>> future = new CompletableFuture<>();
        EventListener<DocumentSnapshot> snapshotListener = (snapshot, exception) -> {
            if (nonNull(exception)) {
                log.error(exception.getMessage());
                future.completeExceptionally(exception);
                return;
            }
            if (isNull(snapshot)) {
                future.complete(new ArrayList<>());
                return;
            }
            try {
                List<?> children = (List<?>) snapshot.get(childField);
                if (nonNull(children)) {
                    int from = Math.max(0, children.size() - limit - offset);
                    int to = children.size() - offset;
                    List<?> result = children.subList(from, to);
                    List<T> castedResult = mapToList(result, element -> objectMapper.convertValue(element, type));
                    future.complete(castedResult);
                }
                future.complete(new ArrayList<>());
            } catch (Exception mappingException) {
                log.error(mappingException.getMessage());
                future.completeExceptionally(mappingException);
            }
        };
        api.collection(collection)
           .document(reference)
           .addSnapshotListener(snapshotListener);
        return future.join();
    }
}
