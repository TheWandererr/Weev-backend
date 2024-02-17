package com.pivo.weev.backend.domain.service.websocket;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MeetMapper;
import com.pivo.weev.backend.domain.mapping.domain.UserMapper;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepository;
import com.pivo.weev.backend.domain.service.event.factory.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.message.NotificationService;
import com.pivo.weev.backend.domain.utils.Constants.NotificationDetails;
import com.pivo.weev.backend.domain.utils.Constants.NotificationTopics;
import com.pivo.weev.backend.integration.firebase.model.chat.Chat;
import com.pivo.weev.backend.integration.firebase.service.FirebaseChatService;
import com.pivo.weev.backend.integration.mapping.domain.chat.ChatMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final MeetRepository meetRepository;

    private final FirebaseChatService firebaseChatService;
    private final NotificationService notificationService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApplicationEventFactory applicationEventFactory;

    public void createChat(UserJpa creatorJpa, MeetJpa meetJpa) {
        User creator = getMapper(UserMapper.class).map(creatorJpa);
        Meet meet = getMapper(MeetMapper.class).map(meetJpa);
        Chat firebaseChat = build(creator, meet);
        firebaseChatService.createChat(firebaseChat);
        notify(meetJpa, creatorJpa, NotificationTopics.CHAT_CREATED, firebaseChat.getId());

        WebSocketEvent webSocketEvent = applicationEventFactory.buildWebSocketEvent(
                getMapper(ChatMapper.class).map(firebaseChat),
                creator.getNickname(),
                EventType.CHAT_CREATED
        );
        applicationEventPublisher.publishEvent(webSocketEvent);
    }

    private Chat build(User creator, Meet meet) {
        Chat chat = new Chat();
        chat.setCreatorId(String.valueOf(creator.getId()));
        chat.addUser(getMapper(com.pivo.weev.backend.integration.mapping.firebase.chat.ChatUserMapper.class).map(creator));
        chat.setName(meet.getHeader());
        chat.setId(meet.getId());
        if (meet.hasPhoto()) {
            chat.setAvatarUrl(meet.getPhoto().getUrl());
        }
        return chat;
    }

    private void notify(MeetJpa meet, UserJpa recipient, String topic, Long chatId) {
        Map<String, Object> details = Map.of(NotificationDetails.CHAT_ID, chatId);
        notificationService.notify(meet, recipient, topic, details);
    }

   /* private void pushMessage(Long chatId, ChatMessage message) {
        firebaseChatService.pushMessage(chatId, getMapper(ChatMessageMapper.class).map(message));
        pushNotify(chatId, message);
    }

    public void pushNotify(Long chatId, ChatMessage message) {
        if (!message.isEvent()) {
            MeetJpa meet = meetRepository.fetch(chatId);
            pushNotify(meet, meet.getMembersWithCreator(), CHAT_NEW_MESSAGE, chatId, message.getText());
        }
    }

    private void pushNotify(MeetJpa meet, Set<UserJpa> recipients, String topic, Long chatId, String text) {
        Map<String, Object> details = Map.of(NotificationDetails.CHAT_ID, chatId, NotificationDetails.TEXT, text);
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(meet, recipients, topic, details);
        applicationEventPublisher.publishEvent(event);
    }

    private ChatMessage createMessage(String code, String text, String type, ChatUser from) {
        ChatMessage message = new ChatMessage();
        message.setCode(code);
        message.setText(text);
        message.setType(type);
        message.setFrom(from);
        return message;
    }*/

}
