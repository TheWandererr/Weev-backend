package com.pivo.weev.backend.domain.service.messaging;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.ChatPrefixes.GROUP;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.CHAT_NEW_MESSAGE;
import static com.pivo.weev.backend.utils.CollectionUtils.collectAsync;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.utils.CollectionUtils.subList;
import static java.lang.Math.min;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
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
        firebaseChatService.saveFirebaseChatInfo(firebaseChat, creator.getId());
        saveNotification(meet, creator, NotificationTopics.CHAT_CREATED, EventType.CHAT_CREATED, firebaseChat);
    }

    private FirebaseChat buildFirebaseChat(UserJpa creator, MeetJpa meet) {
        FirebaseChat firebaseChat = new FirebaseChat();
        firebaseChat.setCreatorId(creator.getId());
        firebaseChat.setName(meet.getHeader());
        firebaseChat.setId(GROUP + meet.getId());
        if (meet.hasPhoto()) {
            firebaseChat.setAvatarUrl(meet.getPhoto().getUrl());
        }
        return firebaseChat;
    }

    private void saveNotification(MeetJpa meet, UserJpa recipient, String topic, EventType eventType, FirebaseChat firebaseChat) {
        Map<String, Object> details = Map.of(NotificationDetails.CHAT_ID, firebaseChat.getId());
        notificationService.notify(meet, recipient, topic, details);
        Chat chat = getMapper(ChatMapper.class).map(firebaseChat);

        WebSocketEvent webSocketEvent = applicationEventFactory.buildWebSocketEvent(chat, recipient.getNickname(), eventType);
        applicationEventPublisher.publishEvent(webSocketEvent);
    }

    @Transactional
    public UserMessage pushMessage(String chatId, String senderNickname, UserMessage message) {
        if (chatId.startsWith(GROUP)) {
            return pushGroupMessage(chatId, senderNickname, message);
        }
        return message;
    }

    private UserMessage pushGroupMessage(String chatId, String senderNickname, UserMessage message) {
        long meetId = toLong(substringAfter(chatId, GROUP));
        MeetJpa meet = meetSearchService.fetchJpa(meetId);
        UserJpa sender = meet.hasCreator(senderNickname) ? meet.getCreator() : userResourceService.fetchJpa(senderNickname, NICKNAME);

        operationValidator.validateSendMessage(meet, sender);

        setFrom(message, sender);
        firebaseChatService.pushMessage(chatId, getMapper(FirebaseChatMessageMapper.class).map(message));

        sendPushNotification(meet, message);

        return message;
    }

    private void setFrom(UserMessage message, UserJpa sender) {
        ChatUser chatUser = getMapper(ChatUserMapper.class).map(sender);
        message.setFrom(chatUser);
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

    public List<Chat> getChats(Long userid, Integer chatsOffset, Integer chatsLimit, Integer historySize) {
        List<FirebaseChat> firebaseChats = firebaseChatService.getChats(userid, chatsOffset, chatsLimit);
        return mapFirebaseChats(firebaseChats, historySize);
    }

    private List<Chat> mapFirebaseChats(List<FirebaseChat> firebaseChats, Integer historySize) {
        Map<FirebaseChat, List<FirebaseChatMessage>> chatsHistory = getChatsHistory(firebaseChats, historySize);
        Map<Long, UserJpa> senders = collectSenders(chatsHistory.values());
        return mapFirebaseChats(chatsHistory, senders);
    }

    private Map<FirebaseChat, List<FirebaseChatMessage>> getChatsHistory(List<FirebaseChat> firebaseChats, Integer historySize) {
        return collectAsync(firebaseChats, toMap(chat -> chat, chat -> extractHistory(chat, historySize)));
    }

    private List<FirebaseChatMessage> extractHistory(FirebaseChat chat, Integer historySize) {
        List<FirebaseChatMessage> firebaseMessages = chat.getMessages();
        int to = firebaseMessages.size();
        int from = to - min(historySize, to);
        return subList(firebaseMessages, from, to);
    }

    private Map<Long, UserJpa> collectSenders(Collection<List<FirebaseChatMessage>> chatsHistory) {
        Set<Long> senderIds = collectSenderIds(chatsHistory);
        return collectAsync(userResourceService.fetchAllJpa(senderIds), toMap(UserJpa::getId, Function.identity()));
    }

    private Set<Long> collectSenderIds(Collection<List<FirebaseChatMessage>> chatsHistory) {
        return chatsHistory.stream()
                           .flatMap(Collection::stream)
                           .map(FirebaseChatMessage::getFrom)
                           .collect(Collectors.toSet());
    }

    private List<Chat> mapFirebaseChats(Map<FirebaseChat, List<FirebaseChatMessage>> chatsHistory, Map<Long, UserJpa> senders) {
        List<Chat> chats = new ArrayList<>();
        chatsHistory.forEach((firebaseChat, history) -> {
            Chat chat = getMapper(ChatMapper.class).map(firebaseChat);
            List<CommonChatMessage> chatMessages = mapToList(history, firebaseChatMessage -> mapFirebaseChatMessage(firebaseChatMessage, senders));
            chat.setMessages(chatMessages);
            chats.add(chat);
        });
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
    public List<CommonChatMessage> getChatMessages(String chatId, Integer offset, Integer historySize) {
        if (chatId.startsWith(GROUP)) {
            return getGroupChatMessages(chatId, offset, historySize);
        }
        return new ArrayList<>();
    }

    private List<CommonChatMessage> getGroupChatMessages(String chatId, Integer offset, Integer historySize) {
        MeetJpa meet = meetSearchService.fetchJpa(toLong(substringAfter(chatId, GROUP)));
        Long userId = getUserId();
        UserJpa requester = meet.hasCreator(userId) ? meet.getCreator() : userResourceService.fetchJpa(userId);
        operationValidator.validateGetChatMessages(meet, requester);

        List<FirebaseChatMessage> history = firebaseChatService.getChatMessages(chatId, offset, historySize);
        Map<Long, UserJpa> senders = collectSenders(singletonList(history));
        return mapToList(history, firebaseChatMessage -> mapFirebaseChatMessage(firebaseChatMessage, senders));
    }
}
