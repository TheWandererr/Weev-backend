package com.pivo.weev.backend.integration.firebase.service;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Collections.CHATS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Collections.USER_CHATS_REFERENCES;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.CHAT_IDS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.LAST_UPDATE;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.MESSAGES;
import static com.pivo.weev.backend.utils.CollectionUtils.last;
import static com.pivo.weev.backend.utils.CollectionUtils.subList;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.integration.firebase.client.FirestoreClient;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChat;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseUserChatsReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseChatService {

    private final FirestoreClient firestoreClient;

    @Async("firebaseFirestoreExecutor")
    public void saveFirebaseChatInfo(FirebaseChat firebaseChat, Long userId) {
        createChat(firebaseChat);
        findUserChatsReference(userId).thenAccept(existingUserChatsReference -> {
            if (nonNull(existingUserChatsReference)) {
                existingUserChatsReference.addReference(firebaseChat.getId());
                updateUserChatsReference(existingUserChatsReference);
            } else {
                createUserChatsReference(new FirebaseUserChatsReference(userId, List.of(firebaseChat.getId())));
            }
        });
    }

    private void createChat(FirebaseChat firebaseChat) {
        firestoreClient.save(CHATS, firebaseChat.getId(), firebaseChat);
    }

    private CompletableFuture<FirebaseUserChatsReference> findUserChatsReference(Long userId) {
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
        firestoreClient.find(CHATS, chatId, FirebaseChat.class)
                       .thenAccept(firebaseChat -> pushMessage(firebaseChat, message));
    }

    private void pushMessage(FirebaseChat firebaseChat, FirebaseChatMessage message) {
        setOrdinalForNextMessage(message, firebaseChat.getMessages());
        firebaseChat.addMessage(message);
        firestoreClient.update(CHATS, firebaseChat.getId(), Map.of(MESSAGES, firebaseChat.getMessages(), LAST_UPDATE, firebaseChat.getLastUpdate()));
    }

    private void setOrdinalForNextMessage(FirebaseChatMessage nextMessage, List<FirebaseChatMessage> chatMessages) {
        FirebaseChatMessage lastMessage = last(chatMessages);
        Long ordinal = nonNull(lastMessage) ? lastMessage.getOrdinal() + 1 : 1;
        nextMessage.setOrdinal(ordinal);
    }

    public List<FirebaseChat> getChats(Long userId, Integer offset, Integer limit) {
        return findUserChatsReference(userId)
                .thenApply(firebaseUserChatsReference -> firestoreClient.findAll(CHATS, firebaseUserChatsReference.getChatIds(), LAST_UPDATE, offset, limit, FirebaseChat.class)
                                                                        .join())
                .join();
    }

    public List<FirebaseChatMessage> getChatMessages(String chatId, Integer offset, Integer historySize) {
        return firestoreClient.find(CHATS, chatId, FirebaseChat.class)
                              .thenApply(chat -> subList(chat.getMessages(), offset, historySize))
                              .join();
    }
}
