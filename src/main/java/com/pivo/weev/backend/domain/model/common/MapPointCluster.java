package com.pivo.weev.backend.domain.model.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MapPointCluster {

    private MapPoint center;
    private List<MapPoint> points;
}
