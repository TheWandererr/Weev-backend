package com.pivo.weev.backend.rest.model.response;

import static java.util.Objects.isNull;

import com.pivo.weev.backend.rest.model.messaging.ChatRest;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatsResponse {

    private List<ChatRest> chats;

    public List<ChatRest> getChats() {
        if (isNull(chats)) {
            chats = new ArrayList<>();
        }
        return chats;
    }
}
