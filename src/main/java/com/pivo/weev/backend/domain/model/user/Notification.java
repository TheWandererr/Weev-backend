package com.pivo.weev.backend.domain.model.user;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {

    private Long id;
    private String topic;
    private String type;
    private boolean viewed;
    private Map<String, Object> details;
}
