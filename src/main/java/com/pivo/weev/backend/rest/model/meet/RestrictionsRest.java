package com.pivo.weev.backend.rest.model.meet;

import static com.pivo.weev.backend.utils.Constants.MeetAvailabilities.PUBLIC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestrictionsRest {

    private boolean joinAfterStartDisallowed;
    private String availability;

    public static RestrictionsRest withDefaults() {
        RestrictionsRest restrictions = new RestrictionsRest();
        restrictions.setAvailability(PUBLIC);
        return restrictions;
    }
}
