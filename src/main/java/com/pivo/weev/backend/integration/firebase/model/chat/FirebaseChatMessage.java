package com.pivo.weev.backend.integration.firebase.model.chat;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseChatMessage implements Serializable {

    private String text;
    private String code;
    private String type;
    private Long from;
    private Long createdAt;
}
