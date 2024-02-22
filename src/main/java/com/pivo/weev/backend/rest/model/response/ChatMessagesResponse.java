package com.pivo.weev.backend.rest.model.response;

import static java.util.Objects.isNull;

import com.pivo.weev.backend.rest.model.messaging.ChatMessageRest;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessagesResponse {

    private List<ChatMessageRest> messages;

    public List<ChatMessageRest> getMessages() {
        if (isNull(messages)) {
            messages = new ArrayList<>();
        }
        return messages;
    }
}
