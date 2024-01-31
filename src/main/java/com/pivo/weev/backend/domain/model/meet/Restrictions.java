package com.pivo.weev.backend.domain.model.meet;

import static com.pivo.weev.backend.domain.model.meet.Restrictions.Availability.PUBLIC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Restrictions {

    private boolean joinAfterStartDisallowed;
    private Availability availability;

    public enum Availability {
        PUBLIC,
        RESTRICTED,
        PRIVATE
    }

    public boolean isPublic() {
        return availability == PUBLIC;
    }
}
