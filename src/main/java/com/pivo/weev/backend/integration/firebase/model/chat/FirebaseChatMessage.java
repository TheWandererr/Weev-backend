package com.pivo.weev.backend.integration.firebase.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseChatMessage {

    private String chatId;
    private String text;
    private String event;
    private String type;
    private FirebaseChatUser from;
    private Long createdAt;
    private Long ordinal;
}
