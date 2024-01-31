package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetsSearchResponse extends PageableResponse<MeetCompactedRest> {

    public MeetsSearchResponse(PageRest<MeetCompactedRest> page, Long totalElements, Integer totalPages) {
        super(page, totalElements, totalPages);
    }
}
