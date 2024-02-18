package com.pivo.weev.backend.domain.service.messaging;

import static com.pivo.weev.backend.domain.model.messaging.chat.CommonMessage.Type.MESSAGE;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.CHAT_NEW_MESSAGE;
import static com.pivo.weev.backend.utils.CollectionUtils.collect;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toMap;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.ChatUserMapper;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.messaging.chat.Chat;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.event.factory.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import com.pivo.weev.backend.domain.service.validation.ChatOperationValidator;
import com.pivo.weev.backend.domain.utils.Constants.NotificationDetails;
import com.pivo.weev.backend.domain.utils.Constants.NotificationTopics;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChat;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseUserChatsReference;
import com.pivo.weev.backend.integration.firebase.service.FirebaseChatService;
import com.pivo.weev.backend.integration.mapping.domain.chat.ChatMapper;
import com.pivo.weev.backend.integration.mapping.domain.chat.ChatMessageMapper;
import com.pivo.weev.backend.integration.mapping.firebase.chat.FirebaseChatMessageMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final ChatOperationValidator operationValidator;

    public void createChat(UserJpa creator, MeetJpa meet) {
        FirebaseChat firebaseChat = buildFirebaseChat(creator, meet);
        FirebaseUserChatsReference firebaseUserChatsReference = new FirebaseUserChatsReference(creator.getId(), List.of(firebaseChat.getId()));
        createFirebaseChatInfo(firebaseChat, firebaseUserChatsReference);

        notify(meet, creator, NotificationTopics.CHAT_CREATED, firebaseChat.getId());

        Chat chat = getMapper(ChatMapper.class).map(firebaseChat);
        WebSocketEvent webSocketEvent = applicationEventFactory.buildWebSocketEvent(
                chat,
                creator.getNickname(),
                EventType.CHAT_CREATED
        );
        applicationEventPublisher.publishEvent(webSocketEvent);
    }

    private FirebaseChat buildFirebaseChat(UserJpa creator, MeetJpa meet) {
        FirebaseChat firebaseChat = new FirebaseChat();
        firebaseChat.setCreatorId(creator.getId());
        firebaseChat.setName(meet.getHeader());
        firebaseChat.setId(meet.getId());
        if (meet.hasPhoto()) {
            firebaseChat.setAvatarUrl(meet.getPhoto().getUrl());
        }
        firebaseChat.setUsers(1);
        return firebaseChat;
    }

    @Async("firebaseFirestoreExecutor")
    protected void createFirebaseChatInfo(FirebaseChat firebaseChat, FirebaseUserChatsReference firebaseUserChatsReference) {
        firebaseChatService.createChat(firebaseChat);
        firebaseChatService.createUserChatsReference(firebaseUserChatsReference);
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

    @Async("firebaseFirestoreExecutor")
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
        Map<String, Object> payload = Map.of(NotificationDetails.CHAT_ID, meet.getId(), NotificationDetails.TEXT, text);
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(meet, recipients, topic, payload);
        applicationEventPublisher.publishEvent(event);
    }

    private void setProperties(ChatMessage message, UserJpa sender) {
        ChatUser chatUser = getMapper(ChatUserMapper.class).map(sender);
        message.setFrom(chatUser);
        message.setType(MESSAGE);
    }

    public List<Chat> getChats(Long userId, Integer historySize) {
        List<Long> firebaseChatIds = firebaseChatService.getChatIds(userId);
        List<FirebaseChat> firebaseChats = firebaseChatService.getChats(firebaseChatIds);
        Map<Long, List<FirebaseChatMessage>> chatsHistory = getChatsHistory(firebaseChats, historySize);
        Map<Long, UserJpa> senders = collectSenders(chatsHistory);

        List<Chat> chats = new ArrayList<>();
        for (FirebaseChat firebaseChat : firebaseChats) {
            Chat chat = getMapper(ChatMapper.class).map(firebaseChat);

            List<FirebaseChatMessage> history = chatsHistory.get(firebaseChat.getId());
            for (FirebaseChatMessage firebaseChatMessage : history) {
                ChatMessage chatMessage = getMapper(ChatMessageMapper.class).map(firebaseChatMessage);
                UserJpa sender = senders.get(firebaseChatMessage.getFrom());
                chatMessage.setFrom(getMapper(ChatUserMapper.class).map(sender));
                chat.addMessage(chatMessage);
            }

            chats.add(chat);
        }
        return chats;
    }

    private Map<Long, List<FirebaseChatMessage>> getChatsHistory(List<FirebaseChat> firebaseChats, Integer historySize) {
        return collect(firebaseChats, toMap(FirebaseChat::getId, chat -> extractHistory(chat, historySize)));
    }

    private List<FirebaseChatMessage> extractHistory(FirebaseChat chat, Integer historySize) {
        List<FirebaseChatMessage> firebaseMessages = chat.getMessages();
        return firebaseMessages.subList(firebaseMessages.size() - min(historySize, firebaseMessages.size()), firebaseMessages.size());
    }

    private Map<Long, UserJpa> collectSenders(Map<Long, List<FirebaseChatMessage>> chatsHistory) {
        Set<Long> senderIds = collectSenderIds(chatsHistory);
        return collect(userResourceService.fetchAllJpa(senderIds), toMap(UserJpa::getId, Function.identity()));
    }

    private Set<Long> collectSenderIds(Map<Long, List<FirebaseChatMessage>> chatsHistory) {
        return chatsHistory.values().stream()
                           .flatMap(Collection::stream)
                           .map(FirebaseChatMessage::getFrom)
                           .collect(Collectors.toSet());
    }

}
