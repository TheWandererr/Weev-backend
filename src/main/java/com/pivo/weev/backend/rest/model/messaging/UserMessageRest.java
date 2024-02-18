package com.pivo.weev.backend.rest.model.messaging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMessageRest extends CommonChatMessageRest {

    private ChatUserRest from;
}
