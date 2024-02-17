package com.pivo.weev.backend.websocket.listener;

import static com.pivo.weev.backend.websocket.utils.StompUtils.getDestination;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.pivo.weev.backend.websocket.model.MessageWs;
import com.pivo.weev.backend.websocket.utils.Constants.SubscriptionDestinations;
import com.pivo.weev.backend.websocket.utils.Constants.UserDestinations;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
public class UpdatesSubscriptionListener {

    private final SimpMessagingTemplate template;

    @EventListener
    public void handleSubscription(SessionSubscribeEvent event) {
        String destination = getDestination(event);
        if (SubscriptionDestinations.UPDATES.equals(destination)) {
            Principal user = event.getUser();
            String userName = ofNullable(user).map(Principal::getName).orElse(EMPTY);
            template.convertAndSendToUser(userName, UserDestinations.UPDATES, new MessageWs("Subscribed to updates"));
        }
    }
}
