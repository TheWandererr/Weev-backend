package com.pivo.weev.backend.domain.model.event;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Radius {

    private MapPoint point;
    private double value;
}
