package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.rest.model.event.SearchContextRest;
import com.pivo.weev.backend.rest.model.request.EventsSearchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SearchParamsMapper {

    @Mapping(target = "published", source = "searchContext.published")
    @Mapping(target = "onModeration", source = "searchContext.onModeration")
    SearchParams map(EventsSearchRequest source, SearchContextRest searchContext);
}
