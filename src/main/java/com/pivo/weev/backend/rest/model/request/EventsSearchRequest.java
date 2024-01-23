package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.rest.utils.Constants.MapParams.MAXIMUM_ZOOM;
import static com.pivo.weev.backend.rest.utils.Constants.MapParams.MINIMAL_ZOOM;
import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.EVENTS_PER_PAGE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_AMOUNT;

import com.pivo.weev.backend.rest.model.event.RadiusRest;
import com.pivo.weev.backend.rest.model.event.RestrictionsRest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventsSearchRequest extends PageableRequest {

    private String header;
    private String category;
    private String subcategory;
    @Valid
    private RadiusRest radius;
    @Min(value = MINIMAL_ZOOM, message = INVALID_AMOUNT)
    @Max(value = MAXIMUM_ZOOM, message = INVALID_AMOUNT)
    private Integer zoom;
    private String geoHash;
    private RestrictionsRest restrictions;

    public EventsSearchRequest(Integer page) {
        super(page, EVENTS_PER_PAGE);
    }
}
