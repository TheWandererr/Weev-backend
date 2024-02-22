package com.pivo.weev.backend.domain.model.event.payload;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatSnapshotPayload implements Serializable {

    private String id;
    private Long creatorId;
    private String name;
    private String avatarUrl;
    private Integer users;
    private Long lastUpdate;
}
