package com.pivo.weev.backend.domain.model.messaging.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage extends CommonMessage {

    private ChatUser from;
}
