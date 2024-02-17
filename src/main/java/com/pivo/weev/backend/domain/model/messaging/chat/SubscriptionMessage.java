package com.pivo.weev.backend.domain.model.messaging.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionMessage extends CommonMessage {

    private Code code;

    public enum Code {
        SUBSCRIBED,
        CHAT_CREATED
    }
}
