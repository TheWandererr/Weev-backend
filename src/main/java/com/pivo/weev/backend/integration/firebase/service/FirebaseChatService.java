package com.pivo.weev.backend.integration.firebase.service;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseDatabase.Children.CHATS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseDatabase.References.COMMON;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.integration.firebase.client.DatabaseClient;
import com.pivo.weev.backend.integration.firebase.model.chat.Chat;
import com.pivo.weev.backend.integration.firebase.model.chat.ChatMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseChatService {

    private final DatabaseClient databaseClient;

    public void createChat(Chat chat) {
        databaseClient.save(COMMON, CHATS, chat.getId(), chat);
    }

    public void pushMessage(Long chatId, ChatMessage message) {
        Chat chat = databaseClient.find(COMMON, CHATS, chatId, Chat.class);
        if (nonNull(chat)) {
            chat.addMessage(message);
            databaseClient.update(COMMON, CHATS, chatId, Map.of("messages", chat.getMessages()));
        }
    }
}
