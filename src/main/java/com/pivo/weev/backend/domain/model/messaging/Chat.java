package com.pivo.weev.backend.domain.model.messaging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {

    private Long id;
    private String creatorId;
    private String name;
    private String avatarUrl;
}
