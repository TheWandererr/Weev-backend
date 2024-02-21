package com.pivo.weev.backend.websocket.model;

import static com.pivo.weev.backend.websocket.utils.Constants.MessageEvents.SUBSCRIBED;
import static com.pivo.weev.backend.websocket.utils.Constants.MessageTypes.EVENT;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMessageWs extends MessageWs {

    private String event;

    public static EventMessageWs subscribed() {
        EventMessageWs message = new EventMessageWs();
        message.setType(EVENT);
        message.setEvent(SUBSCRIBED);
        return message;
    }
}
