package com.pivo.weev.backend.rest.model.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationRest {

  @NotBlank(message = "must be not blank")
  private String country;
  private String state;
  private String city;
  private String street;
  private String road;
  private String block;
  private String building;
  private String flat;
  @NotNull(message = "must be not null")
  private Double lng;
  @NotNull(message = "must be not null")
  private Double ltd;
}
