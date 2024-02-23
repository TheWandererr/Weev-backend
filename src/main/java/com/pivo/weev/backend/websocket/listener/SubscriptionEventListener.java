package com.pivo.weev.backend.websocket.listener;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.ChatPrefixes.GROUP;
import static com.pivo.weev.backend.utils.Constants.Symbols.DOT;
import static com.pivo.weev.backend.websocket.model.EventMessageWs.subscribed;
import static com.pivo.weev.backend.websocket.utils.Constants.SubscriptionDestinations.CHAT;
import static com.pivo.weev.backend.websocket.utils.Constants.SubscriptionDestinations.UPDATES;
import static com.pivo.weev.backend.websocket.utils.StompUtils.getDestination;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.service.websocket.SubscriptionService;
import com.pivo.weev.backend.websocket.mapping.ws.MessageWsMapper;
import com.pivo.weev.backend.websocket.model.MessageWs;
import com.pivo.weev.backend.websocket.utils.Constants.UserDestinations;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
public class SubscriptionEventListener {

    private final SimpMessagingTemplate template;
    private final SubscriptionService subscriptionService;

    @EventListener
    public void handleSubscription(SessionSubscribeEvent event) {
        String destination = getDestination(event);
        if (isBlank(destination)) {
            return;
        }
        Principal user = event.getUser();
        String nickname = ofNullable(user).map(Principal::getName).orElse(EMPTY);
        if (UPDATES.equals(destination)) {
            handleUpdatesSubscription(nickname);
        } else if (destination.startsWith(CHAT)) {
            String chatId = substringAfterLast(destination, DOT);
            if (chatId.startsWith(GROUP)) {
                handleGroupChatSubscription(nickname, chatId);
            }
        }
    }

    private void handleUpdatesSubscription(String nickname) {
        template.convertAndSendToUser(nickname, UserDestinations.UPDATES, subscribed());
    }

    private void handleGroupChatSubscription(String nickname, String chatId) {
        EventMessage message = subscriptionService.handleGroupChatSubscription(chatId, nickname);
        MessageWs messageWs = getMapper(MessageWsMapper.class).map(message);
        template.convertAndSendToUser(nickname, UserDestinations.UPDATES, messageWs);
    }
}
