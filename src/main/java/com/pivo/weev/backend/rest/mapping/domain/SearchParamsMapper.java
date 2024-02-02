package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.rest.mapping.domain.decotator.SearchParamsMapperDecorator;
import com.pivo.weev.backend.rest.model.meet.SearchContextRest;
import com.pivo.weev.backend.rest.model.meet.SearchContextRest.VisibilityCriteriaRest;
import com.pivo.weev.backend.rest.model.request.MeetsSearchRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@DecoratedWith(SearchParamsMapperDecorator.class)
@Mapper(uses = {MapPointMapper.class, BoundingBoxMapper.class})
public interface SearchParamsMapper {

    @Mapping(target = "visibilityCriteria", source = "searchContext.visibilityCriteria")
    @Mapping(target = "mapCriteria", source = "source")
    @Mapping(target = "fieldsCriteria", source = "source")
    @Mapping(target = "pageCriteria", expression = "java(mapPageCriteria(source, searchContext))")
    @Mapping(target = "authorId", source = "searchContext.authorId")
    SearchParams map(MeetsSearchRequest source, SearchContextRest searchContext);

    @Mapping(target = "sortFields", source = "searchContext.pageCriteria.sortFields")
    SearchParams.PageCriteria mapPageCriteria(MeetsSearchRequest source, SearchContextRest searchContext);

    @Mapping(target = "bbox", source = "bbox")
    SearchParams.MapCriteria mapMapCriteria(MeetsSearchRequest source);

    SearchParams.VisibilityCriteria mapVisibilityCriteria(VisibilityCriteriaRest source);

    SearchParams.FieldsCriteria mapFieldsCriteria(MeetsSearchRequest source);
}
