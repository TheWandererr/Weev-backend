package com.pivo.weev.backend.rest.model.messaging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscriptionMessageRest extends ChatMessageRest {

    private String event;
}
