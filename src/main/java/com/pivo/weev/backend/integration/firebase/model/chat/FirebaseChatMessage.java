package com.pivo.weev.backend.integration.firebase.model.chat;

import static java.util.Objects.nonNull;

import java.util.Objects;
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

    public boolean hasFrom(Long id) {
        return nonNull(from) && Objects.equals(from.getId(), id);
    }
}
