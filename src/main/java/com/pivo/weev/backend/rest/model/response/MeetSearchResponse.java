package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.meet.MeetDetailedRest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetSearchResponse extends BaseResponse {

    private MeetDetailedRest meet;
}
