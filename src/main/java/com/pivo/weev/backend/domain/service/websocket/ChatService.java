package com.pivo.weev.backend.domain.service.websocket;

import static com.pivo.weev.backend.domain.model.messaging.chat.CommonMessage.Type.MESSAGE;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.CHAT_NEW_MESSAGE;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.ChatUserMapper;
import com.pivo.weev.backend.domain.mapping.domain.MeetMapper;
import com.pivo.weev.backend.domain.mapping.domain.UserMapper;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.messaging.chat.Chat;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.event.factory.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.message.NotificationService;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import com.pivo.weev.backend.domain.service.validation.WebSocketOperationValidator;
import com.pivo.weev.backend.domain.utils.Constants.NotificationDetails;
import com.pivo.weev.backend.domain.utils.Constants.NotificationTopics;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChat;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.firebase.service.FirebaseChatService;
import com.pivo.weev.backend.integration.mapping.domain.chat.ChatMapper;
import com.pivo.weev.backend.integration.mapping.firebase.chat.FirebaseChatMessageMapper;
import com.pivo.weev.backend.integration.mapping.firebase.chat.FirebaseChatUserMapper;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final FirebaseChatService firebaseChatService;
    private final NotificationService notificationService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApplicationEventFactory applicationEventFactory;

    private final MeetSearchService meetSearchService;
    private final UserResourceService userResourceService;
    private final WebSocketOperationValidator operationValidator;

    public void createChat(UserJpa creatorJpa, MeetJpa meetJpa) {
        User creator = getMapper(UserMapper.class).map(creatorJpa);
        Meet meet = getMapper(MeetMapper.class).map(meetJpa);

        FirebaseChat firebaseChat = buildFirebaseChat(creator, meet);
        saveFirebaseChat(firebaseChat);

        notify(meetJpa, creatorJpa, NotificationTopics.CHAT_CREATED, firebaseChat.getId());

        Chat chat = getMapper(ChatMapper.class).map(firebaseChat);
        WebSocketEvent webSocketEvent = applicationEventFactory.buildWebSocketEvent(
                chat,
                creator.getNickname(),
                EventType.CHAT_CREATED
        );
        applicationEventPublisher.publishEvent(webSocketEvent);
    }

    @Async("firebaseDatabaseExecutor")
    protected void saveFirebaseChat(FirebaseChat firebaseChat) {
        firebaseChatService.createChat(firebaseChat);
    }

    private FirebaseChat buildFirebaseChat(User creator, Meet meet) {
        FirebaseChat firebaseChat = new FirebaseChat();
        firebaseChat.setCreatorId(String.valueOf(creator.getId()));
        firebaseChat.addUser(getMapper(FirebaseChatUserMapper.class).map(creator));
        firebaseChat.setName(meet.getHeader());
        firebaseChat.setId(meet.getId());
        if (meet.hasPhoto()) {
            firebaseChat.setAvatarUrl(meet.getPhoto().getUrl());
        }
        return firebaseChat;
    }

    private void notify(MeetJpa meet, UserJpa recipient, String topic, Long chatId) {
        Map<String, Object> details = Map.of(NotificationDetails.CHAT_ID, chatId);
        notificationService.notify(meet, recipient, topic, details);
    }

    @Transactional
    public ChatMessage pushMessage(Long chatId, String senderNickname, ChatMessage message) {
        MeetJpa meet = meetSearchService.fetchJpa(chatId);
        UserJpa sender = userResourceService.fetchJpa(senderNickname, NICKNAME);

        operationValidator.validateSendMessage(meet, sender);

        setProperties(message, sender);
        pushMessage(chatId, message);
        sendPushNotification(meet, message);

        return message;
    }

    @Async("firebaseDatabaseExecutor")
    protected void pushMessage(Long chatId, ChatMessage message) {
        FirebaseChatMessage firebaseChatMessage = getMapper(FirebaseChatMessageMapper.class).map(message);
        firebaseChatService.pushMessage(chatId, firebaseChatMessage);
    }

    private void sendPushNotification(MeetJpa meet, ChatMessage message) {
        if (!message.isEvent()) {
            sendPushNotification(meet, meet.getMembersWithCreator(), CHAT_NEW_MESSAGE, message.getText());
        }
    }

    private void sendPushNotification(MeetJpa meet, Set<UserJpa> recipients, String topic, String text) {
        Map<String, Object> details = Map.of(NotificationDetails.CHAT_ID, meet.getId(), NotificationDetails.TEXT, text);
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(meet, recipients, topic, details);
        applicationEventPublisher.publishEvent(event);
    }

    private void setProperties(ChatMessage message, UserJpa sender) {
        ChatUser chatUser = getMapper(ChatUserMapper.class).map(sender);
        message.setFrom(chatUser);
        message.setType(MESSAGE);
    }
}
