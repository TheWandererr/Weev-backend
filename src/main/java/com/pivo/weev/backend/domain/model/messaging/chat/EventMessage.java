package com.pivo.weev.backend.domain.model.messaging.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMessage extends CommonChatMessage {

    private Event event;

    public enum Event {
        SUBSCRIBED,
        CHAT_CREATED
    }

    @Override
    public Type getType() {
        return Type.EVENT;
    }
}
