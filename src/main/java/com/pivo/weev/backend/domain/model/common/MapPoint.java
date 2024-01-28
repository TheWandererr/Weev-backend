package com.pivo.weev.backend.domain.model.common;

import static java.util.Objects.isNull;

import ch.hsr.geohash.GeoHash;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapPoint {

    private double lng;
    private double ltd;
    private GeoHash geoHash;

    public String getGeoHashString() {
        return isNull(geoHash) ? null : geoHash.toBase32();
    }
}
