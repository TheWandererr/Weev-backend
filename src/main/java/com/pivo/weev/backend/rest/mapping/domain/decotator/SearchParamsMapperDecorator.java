package com.pivo.weev.backend.rest.mapping.domain.decotator;

import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.rest.mapping.domain.SearchParamsMapper;
import com.pivo.weev.backend.rest.model.meet.SearchContextRest;
import com.pivo.weev.backend.rest.model.request.MeetsSearchRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class SearchParamsMapperDecorator implements SearchParamsMapper {

    private final SearchParamsMapper delegate;

    @Override
    public SearchParams map(MeetsSearchRequest source, SearchContextRest searchContext) {
        return delegate.map(source, searchContext);
    }
}
