package com.pivo.weev.backend.rest.model.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoundingBoxRest {

    private double southLatitude; // юг x
    private double northLatitude; // север x
    private double westLongitude; // запад y
    private double eastLongitude; // восток y
}
