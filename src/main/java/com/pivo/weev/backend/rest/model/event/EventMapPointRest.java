package com.pivo.weev.backend.rest.model.event;

import com.pivo.weev.backend.rest.model.common.MapPointRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMapPointRest extends MapPointRest {

    private Long eventId;
}
