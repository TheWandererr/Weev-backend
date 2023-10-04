package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.event.EventMapPointRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventsMapPointsSearchResponse extends PageableResponse<EventMapPointRest> {

    public EventsMapPointsSearchResponse(PageRest<EventMapPointRest> page, Long totalElements, Integer totalPages) {
        super(page, totalElements, totalPages);
    }
}
