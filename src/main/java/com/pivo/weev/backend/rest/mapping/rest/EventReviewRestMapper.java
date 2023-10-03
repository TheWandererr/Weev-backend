package com.pivo.weev.backend.rest.mapping.rest;

import static com.pivo.weev.backend.domain.utils.EventDataUtils.hasHiddenData;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.rest.model.event.EventReviewRest;
import com.pivo.weev.backend.rest.model.event.LocationRest;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {ImageRestMapper.class, EntryFeeRestMapper.class, RestrictionsRestMapper.class})
public interface EventReviewRestMapper {

    LocationRestMapper LOCATION_REST_MAPPER = getMapper(LocationRestMapper.class);

    @Mapping(target = "membersCount", expression = "java(source.getMembers().size())")
    @Mapping(target = "ended", expression = "java(source.isEnded())")
    @Mapping(target = "started", expression = "java(source.isStarted())")
    @Mapping(target = "location", ignore = true)
    EventReviewRest map(Event source);

    List<EventReviewRest> map(List<Event> source);

    @AfterMapping
    default void mapPrivateData(Event source, @MappingTarget EventReviewRest target) {
        boolean hidePrivateData = hasHiddenData(source);
        LocationRest restLocation = hidePrivateData
                ? LOCATION_REST_MAPPER.mapHidden(source.getLocation())
                : LOCATION_REST_MAPPER.map(source.getLocation());
        target.setLocation(restLocation);
    }
}
