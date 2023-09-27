package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.EVENTS_PER_PAGE;

import com.pivo.weev.backend.rest.model.event.RadiusRest;
import jakarta.validation.Valid;
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

    public EventsSearchRequest(Integer page) {
        super(page, EVENTS_PER_PAGE);
    }
}
