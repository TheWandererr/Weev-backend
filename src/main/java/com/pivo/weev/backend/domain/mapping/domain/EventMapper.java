package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(uses = {CategoryMapper.class, SubcategoryMapper.class, UserMapper.class, LocationMapper.class, EntryFeeMapper.class, ImageMapper.class})
public interface EventMapper {

    Event map(EventJpa source);

    List<Event> map(List<EventJpa> source);
}
