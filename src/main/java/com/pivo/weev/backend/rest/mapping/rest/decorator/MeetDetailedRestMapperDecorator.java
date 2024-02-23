package com.pivo.weev.backend.rest.mapping.rest.decorator;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getNullableUserId;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.rest.mapping.rest.MeetDetailedRestMapper;
import com.pivo.weev.backend.rest.model.meet.MeetDetailedRest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class MeetDetailedRestMapperDecorator implements MeetDetailedRestMapper {

    private final MeetDetailedRestMapper delegate;

    @Override
    public MeetDetailedRest map(Meet source) {
        MeetDetailedRest destination = delegate.map(source);
        destination.setMembersCount(source.getMembers().size());
        destination.setMember(source.hasMember(getNullableUserId()));
        return destination;
    }
}
