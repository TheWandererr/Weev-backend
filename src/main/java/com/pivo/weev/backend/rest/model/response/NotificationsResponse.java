package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.common.PageRest;
import com.pivo.weev.backend.rest.model.user.NotificationRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationsResponse extends PageableResponse<NotificationRest> {

    public NotificationsResponse(PageRest<NotificationRest> page, Long totalElements, Integer totalPages) {
        super(page, totalElements, totalPages);
    }
}
