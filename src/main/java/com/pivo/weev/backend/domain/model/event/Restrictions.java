package com.pivo.weev.backend.domain.model.event;

import static com.pivo.weev.backend.domain.model.event.Restrictions.Availability.PUBLIC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Restrictions {

    private boolean joinAfterStartDisallowed;
    private Availability availability;

    public enum Availability {
        PUBLIC,
        REQUEST,
        PRIVATE
    }

    public boolean isPublic() {
        return availability == PUBLIC;
    }
}
