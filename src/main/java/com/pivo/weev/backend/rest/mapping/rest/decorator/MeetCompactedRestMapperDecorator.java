package com.pivo.weev.backend.rest.mapping.rest.decorator;

import static com.pivo.weev.backend.rest.utils.MeetViewUtils.hasHiddenData;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.rest.mapping.rest.MeetCompactedRestMapper;
import com.pivo.weev.backend.rest.model.meet.LocationRest;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class MeetCompactedRestMapperDecorator implements MeetCompactedRestMapper {

    private final MeetCompactedRestMapper delegate;

    @Override
    public MeetCompactedRest map(Meet source) {
        MeetCompactedRest destination = delegate.map(source);
        destination.setMembersCount(source.getMembers().size());
        boolean hasHiddenData = hasHiddenData(source);
        if (hasHiddenData) {
            LocationRest location = destination.getLocation();
            destination.setLocation(new LocationRest(location.getCountry(), location.getCity()));
        }
        return destination;
    }
}
