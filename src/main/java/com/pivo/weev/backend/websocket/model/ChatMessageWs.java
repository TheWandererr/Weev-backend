package com.pivo.weev.backend.websocket.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageWs extends CommonMessageWs {

    private UserWs from;
}
