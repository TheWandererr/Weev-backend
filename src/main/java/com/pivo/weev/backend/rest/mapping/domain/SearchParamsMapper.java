package com.pivo.weev.backend.rest.mapping.domain;

import static com.pivo.weev.backend.domain.persistance.utils.PageableUtils.build;

import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.rest.mapping.domain.decotator.SearchParamsMapperDecorator;
import com.pivo.weev.backend.rest.model.meet.SearchContextRest;
import com.pivo.weev.backend.rest.model.meet.SearchContextRest.VisibilityCriteriaRest;
import com.pivo.weev.backend.rest.model.request.MeetsSearchRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;

@DecoratedWith(SearchParamsMapperDecorator.class)
@Mapper(uses = {MapPointMapper.class, BoundingBoxMapper.class})
public interface SearchParamsMapper {

    @Mapping(target = "visibilityCriteria", source = "searchContext.visibilityCriteria")
    @Mapping(target = "mapCriteria", source = "source")
    @Mapping(target = "fieldsCriteria", source = "source")
    @Mapping(target = "pageable", source = "source")
    @Mapping(target = "authorId", source = "searchContext.authorId")
    SearchParams map(MeetsSearchRequest source, SearchContextRest searchContext);

    default Pageable mapPageable(MeetsSearchRequest source) {
        return build(source.getPage(), source.getPageSize(), source.getSortFields());
    }

    @Mapping(target = "bbox", source = "bbox")
    SearchParams.MapCriteria mapMapCriteria(MeetsSearchRequest source);

    SearchParams.VisibilityCriteria mapVisibilityCriteria(VisibilityCriteriaRest source);

    SearchParams.FieldsCriteria mapFieldsCriteria(MeetsSearchRequest source);
}
