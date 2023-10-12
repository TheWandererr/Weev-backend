package com.pivo.weev.backend.rest.model.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapPointClusterRest {

    private MapPointRest center;
    private int size;
}
