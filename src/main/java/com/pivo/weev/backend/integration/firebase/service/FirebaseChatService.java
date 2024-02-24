package com.pivo.weev.backend.integration.firebase.service;

import static com.google.cloud.firestore.Filter.equalTo;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Collections.CHATS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Collections.USER_CHATS_REFERENCES;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.CHAT_IDS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.CREATED_AT;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.LAST_UPDATE;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.MESSAGES;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.ORDINAL;
import static com.pivo.weev.backend.utils.AsyncUtils.async;
import static com.pivo.weev.backend.utils.AsyncUtils.runAll;
import static com.pivo.weev.backend.utils.StreamUtils.parallelStream;
import static java.time.Instant.now;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.model.messaging.CommonMessage.Type;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage.Event;
import com.pivo.weev.backend.domain.utils.Constants.ChatMessageStates;
import com.pivo.weev.backend.domain.utils.Constants.UserStates;
import com.pivo.weev.backend.integration.firebase.client.FirestoreClient;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatSnapshot;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatUser;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseUserChatsReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseChatService {

    private final FirestoreClient firestoreClient;
    private final ThreadPoolTaskExecutor firebaseFirestoreExecutor;

    @Async("firebaseFirestoreExecutor")
    public void saveFirebaseChatInfo(FirebaseChatSnapshot firebaseChatSnapshot, Long userId) {
        createChatSnapshot(firebaseChatSnapshot);
        findUserChatsReference(userId).thenAccept(existingUserChatsReference -> {
            if (nonNull(existingUserChatsReference)) {
                existingUserChatsReference.addReference(firebaseChatSnapshot.getId());
                updateUserChatsReference(existingUserChatsReference);
            } else {
                createUserChatsReference(new FirebaseUserChatsReference(userId, List.of(firebaseChatSnapshot.getId())));
            }
        });
    }

    private void createChatSnapshot(FirebaseChatSnapshot firebaseChatSnapshot) {
        firestoreClient.save(CHATS, firebaseChatSnapshot.getId(), firebaseChatSnapshot);
    }

    public CompletableFuture<FirebaseUserChatsReference> findUserChatsReference(Long userId) {
        return firestoreClient.find(USER_CHATS_REFERENCES, userId.toString(), FirebaseUserChatsReference.class);
    }

    private void updateUserChatsReference(FirebaseUserChatsReference userChatsReference) {
        firestoreClient.update(USER_CHATS_REFERENCES, userChatsReference.getUserId().toString(), Map.of(CHAT_IDS, userChatsReference.getChatIds()));
    }

    private void createUserChatsReference(FirebaseUserChatsReference userChatsReference) {
        firestoreClient.save(USER_CHATS_REFERENCES, userChatsReference.getUserId().toString(), userChatsReference);
    }

    @Async("firebaseFirestoreExecutor")
    public void pushMessage(String chatId, FirebaseChatMessage message) {
        findLastMessage(chatId)
                .thenAccept(lastMessage -> pushMessage(chatId, lastMessage, message));
    }

    private void pushMessage(String chatId, FirebaseChatMessage lastMessage, FirebaseChatMessage newMessage) {
        setOrdinalForNextMessage(newMessage, lastMessage);
        firestoreClient.save(CHATS, chatId, MESSAGES, newMessage, () -> firestoreClient.update(CHATS, chatId, Map.of(LAST_UPDATE, now().toEpochMilli())));
    }

    @Async("firebaseFirestoreExecutor")
    public CompletableFuture<FirebaseChatMessage> findLastMessage(String chatId) {
        return firestoreClient.find(CHATS, chatId, MESSAGES, CREATED_AT, FirebaseChatMessage.class);
    }

    private void setOrdinalForNextMessage(FirebaseChatMessage nextMessage, FirebaseChatMessage lastMessage) {
        Long ordinal = nonNull(lastMessage) ? lastMessage.getOrdinal() + 1 : 1;
        nextMessage.setOrdinal(ordinal);
    }

    @Async("firebaseFirestoreExecutor")
    public CompletableFuture<List<FirebaseChatSnapshot>> getChats(Long userId) {
        return findUserChatsReference(userId)
                .thenCompose(reference -> nonNull(reference)
                        ? async(firebaseFirestoreExecutor, () -> firestoreClient.findAll(CHATS, reference.getChatIds(), LAST_UPDATE, FirebaseChatSnapshot.class))
                        : new CompletableFuture<>())
                .join();
    }

    @Async("firebaseFirestoreExecutor")
    public CompletableFuture<List<FirebaseChatMessage>> getChatMessages(String chatId, Integer offset, Integer limit) {
        return firestoreClient.findAll(CHATS, chatId, MESSAGES, CREATED_AT, offset, limit, FirebaseChatMessage.class);
    }

    @Async("firebaseFirestoreExecutor")
    public CompletableFuture<List<FirebaseChatMessage>> getChatMessages(String chatId) {
        return firestoreClient.findAll(CHATS, chatId, MESSAGES, FirebaseChatMessage.class);
    }

    public void deleteChatsMessages(Long userId, List<List<FirebaseChatMessage>> chatsMessages) {
        var jobs = parallelStream(chatsMessages)
                .flatMap(Collection::stream)
                .filter(message -> message.hasFrom(userId))
                .map(message -> (Runnable) () -> deleteMessage(message))
                .toList();
        runAll(firebaseFirestoreExecutor, jobs, throwable -> {
            log.error("Error deleting chat messages {}", throwable.getMessage());
            return null;
        });
    }

    private void deleteMessage(FirebaseChatMessage message) {
        FirebaseChatUser from = message.getFrom();
        from.setNickname(UserStates.DELETED);
        from.setAvatarUrl(null);
        message.setText(ChatMessageStates.DELETED);
        message.setType(Type.EVENT.name());
        message.setEvent(Event.DELETED.name());
        firestoreClient.update(CHATS, message.getChatId(), MESSAGES, equalTo(ORDINAL, message.getOrdinal()), message);
    }
}
