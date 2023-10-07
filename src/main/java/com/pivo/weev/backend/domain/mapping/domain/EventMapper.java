package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {CategoryMapper.class, SubcategoryMapper.class, UserMapper.class, LocationMapper.class, EntryFeeMapper.class, ImageMapper.class, RestrictionsMapper.class})
public interface EventMapper {

    @Mapping(target = "status", source = "source.status")
    Event map(EventJpa source);

    List<Event> map(List<EventJpa> source);

    default String mapStatus(EventStatus status) {
        return status.name();
    }
}
