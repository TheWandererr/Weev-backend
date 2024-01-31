package com.pivo.weev.backend.rest.mapping.domain;

import static com.pivo.weev.backend.utils.Constants.Amount.INFINITY;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.meet.CreatableMeet;
import com.pivo.weev.backend.rest.model.request.MeetSaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {LocationMapper.class})
public interface CreatableMeetMapper {

    @Mapping(target = "membersLimit", source = "source", qualifiedByName = "getMembersLimit")
    @Mapping(target = "startTimeZoneId", ignore = true)
    @Mapping(target = "endTimeZoneId", ignore = true)
    CreatableMeet map(MeetSaveRequest source);

    @Named("getMembersLimit")
    default int getMembersLimit(MeetSaveRequest source) {
        Integer membersLimit = source.getMembersLimit();
        return isNull(membersLimit) ? INFINITY : membersLimit;
    }
}
