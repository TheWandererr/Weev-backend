package com.pivo.weev.backend.domain.model.messaging.chat;

import static com.pivo.weev.backend.domain.model.messaging.CommonMessage.Type.MESSAGE;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMessage extends ChatMessage {

    private ChatUser from;

    @Override
    public Type getType() {
        return MESSAGE;
    }
}
