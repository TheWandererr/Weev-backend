package com.pivo.weev.backend.rest.model.meet;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_NULL_ERROR;

import com.pivo.weev.backend.rest.model.common.MapPointRest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocationRest {

    @NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
    private String country;
    private String state;
    private String city;
    private String street;
    private String road;
    private String block;
    private String building;
    private String flat;
    @NotNull(message = MUST_BE_NOT_NULL_ERROR)
    private MapPointRest point;

    public LocationRest(String country, String city) {
        this.country = country;
        this.city = city;
    }
}
