package com.pivo.weev.backend.domain.model.user;

import com.pivo.weev.backend.domain.model.common.Modifiable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification extends Modifiable {

    private Long id;
    private String topic;
    private String type;
    private boolean read;
    private Map<String, Object> details;
}
