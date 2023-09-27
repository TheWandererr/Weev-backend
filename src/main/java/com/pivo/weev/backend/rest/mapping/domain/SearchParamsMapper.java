package com.pivo.weev.backend.rest.mapping.domain;

import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.EVENTS_PER_PAGE;

import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.rest.model.event.SearchContextRest;
import com.pivo.weev.backend.rest.model.request.EventsSearchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface SearchParamsMapper {

    @Mapping(target = "published", source = "searchContext.published")
    @Mapping(target = "onModeration", source = "searchContext.onModeration")
    @Mapping(target = "pageSize", source = "source", qualifiedByName = "extractPageSize")
    SearchParams map(EventsSearchRequest source, SearchContextRest searchContext);

    @Named("extractPageSize")
    default Integer extractPageSize(EventsSearchRequest source) {
        return source.hasPageSize() ? source.getPageSize() : EVENTS_PER_PAGE;
    }
}
