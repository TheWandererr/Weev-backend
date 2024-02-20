package com.pivo.weev.backend.websocket.model;

import static com.pivo.weev.backend.websocket.utils.Constants.MessageEvents.SUBSCRIBED;
import static com.pivo.weev.backend.websocket.utils.Constants.MessageTypes.EVENT;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionMessageWs extends CommonMessageWs {

    private String event;

    public static SubscriptionMessageWs subscribed() {
        SubscriptionMessageWs message = new SubscriptionMessageWs();
        message.setType(EVENT);
        message.setEvent(SUBSCRIBED);
        return message;
    }
}
