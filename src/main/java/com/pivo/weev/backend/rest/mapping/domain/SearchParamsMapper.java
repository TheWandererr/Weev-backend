package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.rest.model.event.SearchContextRest;
import com.pivo.weev.backend.rest.model.event.SearchContextRest.VisibilityCriteriaRest;
import com.pivo.weev.backend.rest.model.request.EventsSearchRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {MapPointMapper.class})
public interface SearchParamsMapper {

    @Mapping(target = "visibilityCriteria", source = "searchContext.visibilityCriteria")
    @Mapping(target = "mapCriteria", source = "source")
    @Mapping(target = "fieldsCriteria", source = "source")
    @Mapping(target = "pageCriteria", expression = "java(mapPageCriteria(source, searchContext))")
    @Mapping(target = "authorId", source = "searchContext.authorId")
    SearchParams map(EventsSearchRequest source, SearchContextRest searchContext);

    @Mapping(target = "sortFields", source = "searchContext.pageCriteria.sortFields")
    SearchParams.PageCriteria mapPageCriteria(EventsSearchRequest source, SearchContextRest searchContext);

    SearchParams.MapCriteria mapMapCriteria(EventsSearchRequest source);

    SearchParams.VisibilityCriteria mapVisibilityCriteria(VisibilityCriteriaRest source);

    SearchParams.FieldsCriteria mapFieldsCriteria(EventsSearchRequest source);
}
