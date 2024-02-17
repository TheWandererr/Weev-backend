package com.pivo.weev.backend.integration.firebase.service;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseDatabase.Children.CHATS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseDatabase.References.COMMON;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.integration.firebase.client.DatabaseClient;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChat;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseChatService {

    private final DatabaseClient databaseClient;

    public void createChat(FirebaseChat firebaseChat) {
        databaseClient.save(COMMON, CHATS, firebaseChat.getId(), firebaseChat);
    }

    public void pushMessage(Long chatId, FirebaseChatMessage message) {
        FirebaseChat firebaseChat = databaseClient.find(COMMON, CHATS, chatId, FirebaseChat.class);
        if (nonNull(firebaseChat)) {
            firebaseChat.addMessage(message);
            databaseClient.update(COMMON, CHATS, chatId, Map.of("messages", firebaseChat.getMessages()));
        }
    }
}
