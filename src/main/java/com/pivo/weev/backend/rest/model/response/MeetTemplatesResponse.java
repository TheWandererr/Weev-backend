package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.meet.MeetTemplateRest;

public class MeetTemplatesResponse extends PageableResponse<MeetTemplateRest> {

    public MeetTemplatesResponse(PageRest<MeetTemplateRest> page, Long totalElements, Integer totalPages) {
        super(page, totalElements, totalPages);
    }
}
