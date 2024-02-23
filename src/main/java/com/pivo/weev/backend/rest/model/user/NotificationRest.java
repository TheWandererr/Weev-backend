package com.pivo.weev.backend.rest.model.user;

import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.common.Modifiable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRest extends Modifiable {

    private Long id;
    private String topic;
    private String type;
    private boolean read;
    private Map<String, Object> details;

    public Map<String, Object> getDetails() {
        if (isNull(details)) {
            details = new HashMap<>();
        }
        return details;
    }
}
