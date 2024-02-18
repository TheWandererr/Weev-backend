package com.pivo.weev.backend.domain.service.messaging;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.CHAT_NEW_MESSAGE;
import static com.pivo.weev.backend.utils.CollectionUtils.collect;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static java.lang.Math.min;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.ChatUserMapper;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.messaging.chat.Chat;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.domain.model.messaging.chat.CommonChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
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
import com.pivo.weev.backend.integration.mapping.domain.chat.CommonChatMessageMapper;
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

        processFirebaseChatInfo(firebaseChat, creator.getId());

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

    private void processFirebaseChatInfo(FirebaseChat firebaseChat, Long userId) {
        FirebaseUserChatsReference userChatsReference = firebaseChatService.findUserChatsReference(userId);
        if (nonNull(userChatsReference)) {
            userChatsReference.getChatIds().add(firebaseChat.getId());
            updateFirebaseChatInfo(firebaseChat, userChatsReference);
        } else {
            FirebaseUserChatsReference firebaseUserChatsReference = new FirebaseUserChatsReference(userId, List.of(firebaseChat.getId()));
            createFirebaseChatInfo(firebaseChat, firebaseUserChatsReference);
        }
    }

    @Async("firebaseFirestoreExecutor")
    protected void createFirebaseChatInfo(FirebaseChat firebaseChat, FirebaseUserChatsReference firebaseUserChatsReference) {
        firebaseChatService.createChat(firebaseChat);
        firebaseChatService.createUserChatsReference(firebaseUserChatsReference);
    }

    @Async("firebaseFirestoreExecutor")
    protected void updateFirebaseChatInfo(FirebaseChat firebaseChat, FirebaseUserChatsReference firebaseUserChatsReference) {
        firebaseChatService.createChat(firebaseChat);
        firebaseChatService.updateUserChatsReference(firebaseUserChatsReference);
    }

    private void notify(MeetJpa meet, UserJpa recipient, String topic, Long chatId) {
        Map<String, Object> details = Map.of(NotificationDetails.CHAT_ID, chatId);
        notificationService.notify(meet, recipient, topic, details);
    }

    @Transactional
    public UserMessage pushMessage(Long chatId, String senderNickname, UserMessage message) {
        MeetJpa meet = meetSearchService.fetchJpa(chatId);
        UserJpa sender = userResourceService.fetchJpa(senderNickname, NICKNAME);

        operationValidator.validateSendMessage(meet, sender);

        setProperties(message, sender);
        pushMessage(chatId, message);
        sendPushNotification(meet, message);

        return message;
    }

    @Async("firebaseFirestoreExecutor")
    protected void pushMessage(Long chatId, UserMessage message) {
        FirebaseChatMessage firebaseChatMessage = getMapper(FirebaseChatMessageMapper.class).map(message);
        firebaseChatService.pushMessage(chatId, firebaseChatMessage);
    }

    private void sendPushNotification(MeetJpa meet, UserMessage message) {
        if (!message.isEvent()) {
            sendPushNotification(meet, meet.getMembersWithCreator(), CHAT_NEW_MESSAGE, message.getText());
        }
    }

    private void sendPushNotification(MeetJpa meet, Set<UserJpa> recipients, String topic, String text) {
        Map<String, Object> payload = Map.of(NotificationDetails.CHAT_ID, meet.getId(), NotificationDetails.TEXT, text);
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(meet, recipients, topic, payload);
        applicationEventPublisher.publishEvent(event);
    }

    private void setProperties(UserMessage message, UserJpa sender) {
        ChatUser chatUser = getMapper(ChatUserMapper.class).map(sender);
        message.setFrom(chatUser);
    }

    public List<Chat> getChats(Long userId, Integer historySize) {
        List<Long> firebaseChatIds = firebaseChatService.getChatIds(userId);
        List<FirebaseChat> firebaseChats = firebaseChatService.getChats(firebaseChatIds);

        Map<Long, List<FirebaseChatMessage>> chatsHistory = getChatsHistory(firebaseChats, historySize);
        Map<Long, UserJpa> senders = collectSenders(chatsHistory);
        return mapFirebaseChats(firebaseChats, chatsHistory, senders);
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

    private List<Chat> mapFirebaseChats(List<FirebaseChat> firebaseChats, Map<Long, List<FirebaseChatMessage>> chatsHistory, Map<Long, UserJpa> senders) {
        List<Chat> chats = new ArrayList<>();
        for (FirebaseChat firebaseChat : firebaseChats) {
            Chat chat = getMapper(ChatMapper.class).map(firebaseChat);
            List<FirebaseChatMessage> history = chatsHistory.get(firebaseChat.getId());
            List<CommonChatMessage> chatMessages = mapToList(history, firebaseChatMessage -> mapFirebaseChatMessage(firebaseChatMessage, senders));
            chat.setMessages(chatMessages);
            chats.add(chat);
        }
        return chats;
    }

    private CommonChatMessage mapFirebaseChatMessage(FirebaseChatMessage firebaseChatMessage, Map<Long, UserJpa> senders) {
        CommonChatMessage chatMessage = getMapper(CommonChatMessageMapper.class).map(firebaseChatMessage);
        if (chatMessage instanceof UserMessage userMessage) {
            UserJpa sender = senders.get(firebaseChatMessage.getFrom());
            userMessage.setFrom(getMapper(ChatUserMapper.class).map(sender));
        }
        return chatMessage;
    }

    @Transactional
    public List<CommonChatMessage> getChatMessages(Long chatId, Integer offset, Integer historySize) {
        MeetJpa meet = meetSearchService.fetchJpa(chatId);
        UserJpa requester = userResourceService.fetchJpa(getUserId());
        operationValidator.validateGetChatMessages(meet, requester);

        List<FirebaseChatMessage> history = firebaseChatService.getChatMessages(chatId, offset, historySize);
        Map<Long, UserJpa> senders = collectSenders(Map.of(chatId, history));

        return mapToList(history, firebaseChatMessage -> mapFirebaseChatMessage(firebaseChatMessage, senders));
    }
}
