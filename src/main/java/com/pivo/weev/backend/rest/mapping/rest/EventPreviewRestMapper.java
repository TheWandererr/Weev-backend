package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.rest.model.event.EventPreviewRest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {LocationRestMapper.class, ImageRestMapper.class, EntryFeeRestMapper.class, RestrictionsRestMapper.class})
public interface EventPreviewRestMapper {

    @Mapping(target = "membersCount", expression = "java(source.getMembers().size())")
    @Mapping(target = "ended", expression = "java(source.isEnded())")
    @Mapping(target = "started", expression = "java(source.isStarted())")
    EventPreviewRest map(Event source);

    List<EventPreviewRest> map(List<Event> source);
}
