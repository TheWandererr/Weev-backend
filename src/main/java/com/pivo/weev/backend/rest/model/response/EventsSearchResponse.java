package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.event.EventCompactedRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventsSearchResponse extends PageableResponse<EventCompactedRest> {

    public EventsSearchResponse(PageRest<EventCompactedRest> page, Long totalElements, Integer totalPages) {
        super(page, totalElements, totalPages);
    }
}
