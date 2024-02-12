package com.pivo.weev.backend.integration.firebase.model.chat;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {

    private String text;
    private String code;
    private String type;
    private ChatUser sender;
    private Date createdAt;
}
