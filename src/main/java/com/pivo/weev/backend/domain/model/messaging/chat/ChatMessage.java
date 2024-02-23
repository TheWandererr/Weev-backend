package com.pivo.weev.backend.domain.model.messaging.chat;


import static com.pivo.weev.backend.domain.model.messaging.WsMessage.Type.MESSAGE;

import com.pivo.weev.backend.domain.model.messaging.WsMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage extends WsMessage {

    private String chatId;
    private Long ordinal;

    @Override
    protected Type getType() {
        return MESSAGE;
    }
}
