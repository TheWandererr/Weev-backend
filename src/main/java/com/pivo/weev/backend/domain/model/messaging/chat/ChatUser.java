package com.pivo.weev.backend.domain.model.messaging.chat;

import static com.pivo.weev.backend.utils.Constants.APPLICATION_NAME;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatUser {

    private long id;
    private String nickname;

    public static ChatUser system() {
        return new ChatUser(0, APPLICATION_NAME);
    }
}
