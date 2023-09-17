package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.event.EventPreviewRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventsSearchResponse extends PageableResponse<EventPreviewRest> {

    public EventsSearchResponse(PageRest<EventPreviewRest> page, Long totalElements, Integer totalPages) {
        super(page, totalElements, totalPages);
    }
}
