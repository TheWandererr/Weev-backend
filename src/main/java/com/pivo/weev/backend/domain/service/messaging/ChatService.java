package com.pivo.weev.backend.domain.service.messaging;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.ChatPrefixes.GROUP;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.CHAT_CREATED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.CHAT_NEW_MESSAGE;
import static com.pivo.weev.backend.utils.AsyncUtils.collectAll;
import static com.pivo.weev.backend.utils.CollectionUtils.collect;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.utils.StreamUtils.parallelStream;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.ChatMessagePayloadMapper;
import com.pivo.weev.backend.domain.mapping.domain.ChatUserMapper;
import com.pivo.weev.backend.domain.mapping.domain.MeetPayloadMapper;
import com.pivo.weev.backend.domain.mapping.domain.UserPayloadMapper;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.event.payload.ChatMessagePayload;
import com.pivo.weev.backend.domain.model.event.payload.ChatSnapshotPayload;
import com.pivo.weev.backend.domain.model.event.payload.MeetPayload;
import com.pivo.weev.backend.domain.model.event.payload.UserPayload;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatSnapshot;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.event.factory.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import com.pivo.weev.backend.domain.service.validation.ChatOperationValidator;
import com.pivo.weev.backend.domain.utils.Constants.MessagingPayload;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatSnapshot;
import com.pivo.weev.backend.integration.firebase.service.FirebaseChatService;
import com.pivo.weev.backend.integration.mapping.domain.chat.ChatMessageMapper;
import com.pivo.weev.backend.integration.mapping.domain.chat.ChatSnapshotMapper;
import com.pivo.weev.backend.integration.mapping.domain.chat.ChatSnapshotPayloadMapper;
import com.pivo.weev.backend.integration.mapping.firebase.chat.FirebaseChatMessageMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        FirebaseChatSnapshot firebaseChatSnapshot = buildSnapshot(creator, meet);
        firebaseChatService.saveFirebaseChatInfo(firebaseChatSnapshot, creator.getId());

        ChatSnapshotPayload chatPayload = getMapper(ChatSnapshotPayloadMapper.class).map(firebaseChatSnapshot);
        notificationService.notify(meet, creator, CHAT_CREATED, Map.of(MessagingPayload.CHAT, chatPayload));

        WebSocketEvent webSocketEvent = applicationEventFactory.buildWebSocketEvent(chatPayload, creator.getNickname(), EventType.CHAT_CREATED);
        applicationEventPublisher.publishEvent(webSocketEvent);
    }

    private FirebaseChatSnapshot buildSnapshot(UserJpa creator, MeetJpa meet) {
        FirebaseChatSnapshot firebaseChatSnapshot = new FirebaseChatSnapshot();
        firebaseChatSnapshot.setCreatorId(creator.getId());
        firebaseChatSnapshot.setName(meet.getHeader());
        firebaseChatSnapshot.setId(GROUP + meet.getId());
        if (meet.hasPhoto()) {
            firebaseChatSnapshot.setAvatarUrl(meet.getPhoto().getUrl());
        }
        return firebaseChatSnapshot;
    }

    @Transactional
    public UserMessage pushMessage(String senderNickname, UserMessage message) {
        String chatId = message.getChatId();
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

        message.setFrom(getMapper(ChatUserMapper.class).map(sender));
        FirebaseChatMessage firebaseChatMessage = getMapper(FirebaseChatMessageMapper.class).map(message);
        firebaseChatService.pushMessage(chatId, firebaseChatMessage);

        sendPushNotification(meet, message);

        return message;
    }

    private void sendPushNotification(MeetJpa meet, UserMessage message) {
        if (!message.isEvent()) {
            sendPushNotification(meet, meet.getMembersWithCreator(), CHAT_NEW_MESSAGE, message);
        }
    }

    private void sendPushNotification(MeetJpa meet, Set<UserJpa> recipients, String topic, UserMessage chatMessage) {
        ChatMessagePayload messagePayload = getMapper(ChatMessagePayloadMapper.class).map(chatMessage);
        MeetPayload meetPayload = getMapper(MeetPayloadMapper.class).map(meet);
        Map<String, Object> payload = Map.of(MessagingPayload.MESSAGE, messagePayload, MessagingPayload.MEET, meetPayload);
        Set<UserPayload> recipientsPayload = getMapper(UserPayloadMapper.class).map(recipients);
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(recipientsPayload, topic, payload);
        applicationEventPublisher.publishEvent(event);
    }

    public List<ChatSnapshot> getChatSnapshots(Long userid, Map<String, Long> chatsOrdinals) {
        return firebaseChatService.getChats(userid)
                                  .thenApply(chats -> {
                                      var futures = mapToList(chats, chat -> firebaseChatService.findLastMessage(chat.getId()));
                                      List<FirebaseChatMessage> chatMessages = collectAll(futures);
                                      return mapFirebaseChatSnapshots(chats, chatMessages, chatsOrdinals);
                                  })
                                  .join();
    }

    private List<ChatSnapshot> mapFirebaseChatSnapshots(List<FirebaseChatSnapshot> firebaseChatSnapshots, List<FirebaseChatMessage> lastMessages, Map<String, Long> chatsOrdinals) {
        Map<String, FirebaseChatMessage> chatsLastMessages = collect(lastMessages, toMap(FirebaseChatMessage::getChatId, identity()));
        return parallelStream(firebaseChatSnapshots)
                .map(snapshot -> {
                    FirebaseChatMessage lastFirebaseMessage = chatsLastMessages.get(snapshot.getId());
                    return mapFirebaseChatSnapshot(snapshot, lastFirebaseMessage, chatsOrdinals);
                })
                .collect(toList());
    }

    private ChatSnapshot mapFirebaseChatSnapshot(FirebaseChatSnapshot firebaseChatSnapshot, FirebaseChatMessage lastFirebaseMessage, Map<String, Long> chatsOrdinals) {
        ChatMessage lastMessage = getMapper(ChatMessageMapper.class).map(lastFirebaseMessage);
        ChatSnapshot chatSnapshot = getMapper(ChatSnapshotMapper.class).map(firebaseChatSnapshot);
        chatSnapshot.setLastMessage(lastMessage);
        Long initialOrdinal = ofNullable(lastFirebaseMessage).map(FirebaseChatMessage::getOrdinal).orElse(0L);
        chatSnapshot.setNewMessages(initialOrdinal - chatsOrdinals.getOrDefault(firebaseChatSnapshot.getId(), 0L));
        return chatSnapshot;
    }

    @Transactional
    public List<ChatMessage> getChatMessages(String chatId, Integer offset, Integer limit) {
        if (chatId.startsWith(GROUP)) {
            return getGroupChatMessages(chatId, offset, limit);
        }
        return new ArrayList<>();
    }

    private List<ChatMessage> getGroupChatMessages(String chatId, Integer offset, Integer limit) {
        MeetJpa meet = meetSearchService.fetchJpa(toLong(substringAfter(chatId, GROUP)));
        Long userId = getUserId();
        UserJpa requester = meet.hasCreator(userId) ? meet.getCreator() : userResourceService.fetchJpa(userId);
        operationValidator.validateGetChatMessages(meet, requester);
        return firebaseChatService.getChatMessages(chatId, offset, limit)
                                  .thenApply(messages -> getMapper(ChatMessageMapper.class).map(messages))
                                  .join();
    }
}
