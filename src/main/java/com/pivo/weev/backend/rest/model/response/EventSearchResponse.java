package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.event.EventDetailedRest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventSearchResponse extends BaseResponse {

    private EventDetailedRest event;
}
