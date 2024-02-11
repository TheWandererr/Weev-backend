package com.pivo.weev.backend.integration.firebase.service;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseDatabase.Children.CHATS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseDatabase.References.COMMON;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.integration.firebase.client.DatabaseClient;
import com.pivo.weev.backend.integration.firebase.model.chat.Chat;
import com.pivo.weev.backend.integration.mapping.domain.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseChatService {

    private final DatabaseClient databaseClient;

    public void createChat(User creator, Meet meet) {
        Chat chat = new Chat();
        chat.setCreatorId(String.valueOf(creator.getId()));
        chat.addUser(getMapper(UserMapper.class).map(creator));
        chat.setTopic(meet.getHeader());
        chat.setId(String.valueOf(meet.getId()));
        if (meet.hasPhoto()) {
            chat.setAvatarUrl(meet.getPhoto().getUrl());
        }
        databaseClient.save(COMMON, CHATS, chat.getId(), chat);
    }
}
