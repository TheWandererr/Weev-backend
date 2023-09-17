package com.pivo.weev.backend.rest.mapping.domain;

import static com.pivo.weev.backend.common.utils.Constants.Amount.INFINITY;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.rest.model.request.EventSaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface CreatableEventMapper {

    @Mapping(target = "membersLimit", source = "source", qualifiedByName = "getMembersLimit")
    @Mapping(target = "startTimeZoneId", ignore = true)
    @Mapping(target = "endTimeZoneId", ignore = true)
    CreatableEvent map(EventSaveRequest source);

    @Named("getMembersLimit")
    default int getMembersLimit(EventSaveRequest source) {
        Integer membersLimit = source.getMembersLimit();
        return isNull(membersLimit) ? INFINITY : membersLimit;
    }
}
