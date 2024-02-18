package com.pivo.weev.backend.integration.firebase.service;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Collections.CHATS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Collections.MESSAGES;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Collections.USER_CHATS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.Fields.CHAT_IDS;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.integration.firebase.client.FirestoreClient;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChat;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseUserChatsReference;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseChatService {

    private final FirestoreClient firestoreClient;

    public void createChat(FirebaseChat firebaseChat) {
        firestoreClient.save(CHATS, firebaseChat.getId().toString(), firebaseChat);
    }

    public void pushMessage(Long chatId, FirebaseChatMessage message) {
        FirebaseChat firebaseChat = firestoreClient.find(CHATS, chatId.toString(), FirebaseChat.class);
        if (nonNull(firebaseChat)) {
            firebaseChat.addMessage(message);
            firestoreClient.update(CHATS, chatId.toString(), Map.of(MESSAGES, firebaseChat.getMessages()));
        }
    }

    public List<Long> getChatIds(Long userId) {
        FirebaseUserChatsReference reference = firestoreClient.find(USER_CHATS, userId.toString(), FirebaseUserChatsReference.class);
        return reference.getChatIds();
    }

    public List<FirebaseChat> getChats(List<Long> ids) {
        List<String> references = mapToList(ids, Object::toString);
        return firestoreClient.findAll(CHATS, references, FirebaseChat.class);
    }

    public FirebaseUserChatsReference findUserChatsReference(Long userId) {
        return firestoreClient.find(USER_CHATS, userId.toString(), FirebaseUserChatsReference.class);
    }

    public void createUserChatsReference(FirebaseUserChatsReference userChatsReference) {
        firestoreClient.save(USER_CHATS, userChatsReference.getUserId().toString(), userChatsReference);
    }

    public void updateUserChatsReference(FirebaseUserChatsReference userChatsReference) {
        firestoreClient.update(USER_CHATS, userChatsReference.getUserId().toString(), Map.of(CHAT_IDS, userChatsReference.getChatIds()));
    }

    public List<FirebaseChatMessage> getChatMessages(Long chatId, Integer offset, Integer historySize) {
        return firestoreClient.findAllChildrenPageable(CHATS, chatId.toString(), MESSAGES, offset, historySize, FirebaseChatMessage.class);
    }
}
