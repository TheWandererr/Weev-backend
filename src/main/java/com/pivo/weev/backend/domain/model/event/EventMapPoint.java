package com.pivo.weev.backend.domain.model.event;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMapPoint extends MapPoint {

    private Long eventId;
}
