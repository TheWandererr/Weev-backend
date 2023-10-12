package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.common.MapPointClusterRest;
import com.pivo.weev.backend.rest.model.common.PageRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventsMapPointClusterSearchResponse extends PageableResponse<MapPointClusterRest> {

    public EventsMapPointClusterSearchResponse(PageRest<MapPointClusterRest> page, Long totalElements, Integer totalPages) {
        super(page, totalElements, totalPages);
    }
}
