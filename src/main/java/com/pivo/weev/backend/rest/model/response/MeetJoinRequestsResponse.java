package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.meet.MeetJoinRequestRest;
import lombok.Setter;

@Setter
public class MeetJoinRequestsResponse extends PageableResponse<MeetJoinRequestRest> {

    public MeetJoinRequestsResponse(PageRest<MeetJoinRequestRest> page, Long totalElements, Integer totalPages) {
        super(page, totalElements, totalPages);
    }
}
