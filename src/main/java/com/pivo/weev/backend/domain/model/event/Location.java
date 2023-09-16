package com.pivo.weev.backend.domain.model.event;

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
    private Double lng;
    private Double ltd;
}
