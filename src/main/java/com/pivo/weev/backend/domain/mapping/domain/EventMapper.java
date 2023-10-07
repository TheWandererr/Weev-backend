package com.pivo.weev.backend.domain.mapping.domain;

import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.DELETED;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(uses = {CategoryMapper.class, SubcategoryMapper.class, UserMapper.class, LocationMapper.class, EntryFeeMapper.class, ImageMapper.class, RestrictionsMapper.class})
public interface EventMapper {

    @Named("map")
    default Event map(EventJpa source) {
        if (source.isDeleted()) {
            Event event = new Event();
            event.setStatus(DELETED.name());
            return event;
        }
        return doMap(source);
    }

    Event doMap(EventJpa source);

    @IterableMapping(qualifiedByName = "map")
    List<Event> map(List<EventJpa> source);

    default String mapStatus(EventStatus status) {
        return status.name();
    }
}
