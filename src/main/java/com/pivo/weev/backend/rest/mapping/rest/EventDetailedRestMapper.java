package com.pivo.weev.backend.rest.mapping.rest;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.utils.AuthUtils;
import com.pivo.weev.backend.rest.model.event.EventDetailedRest;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {ImageRestMapper.class, EntryFeeRestMapper.class, RestrictionsRestMapper.class}, imports = AuthUtils.class)
public interface EventDetailedRestMapper {

    EventCompactedRestMapper EVENT_COMPACTED_REST_MAPPER = getMapper(EventCompactedRestMapper.class);

    @Mapping(target = "membersCount", expression = "java(source.getMembers().size())")
    @Mapping(target = "ended", expression = "java(source.isEnded())")
    @Mapping(target = "started", expression = "java(source.isStarted())")
    @Mapping(target = "member", expression = "java(source.hasMember(AuthUtils.getNullableUserId()))")
    @Mapping(target = "location", ignore = true)
    EventDetailedRest map(Event source);

    List<EventDetailedRest> map(List<Event> source);

    @AfterMapping
    default void mapHiddenData(Event source, @MappingTarget EventDetailedRest target) {
        EVENT_COMPACTED_REST_MAPPER.mapHiddenData(source, target);
    }
}
