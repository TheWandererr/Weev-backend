package com.pivo.weev.backend.rest.mapping;

import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.rest.model.request.EventSaveRequest;
import org.mapstruct.Mapper;

@Mapper
public interface EventMapper {

  Event map(EventSaveRequest source);
}
