package com.pivo.weev.backend.domain.model.event;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {

    private String country;
    private String state;
    private String city;
    private String street;
    private String road;
    private String block;
    private String building;
    private String flat;
    private MapPoint point;

    public String getGeoHashString() {
        return isNull(point) ? EMPTY : point.getGeoHashString();
    }
}
