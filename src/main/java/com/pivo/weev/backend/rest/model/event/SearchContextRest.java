package com.pivo.weev.backend.rest.model.event;

import static com.pivo.weev.backend.common.utils.ArrayUtils.toArray;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Columns.EVENT_UTC_START_DATE_TIME;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchContextRest {

    private boolean published;
    private boolean onModeration;

    private String[] sortFields;

    public static SearchContextRest published() {
        SearchContextRest context = new SearchContextRest();
        context.setPublished(true);
        return context;
    }

    public static SearchContextRest onModeration() {
        SearchContextRest context = new SearchContextRest();
        context.setOnModeration(true);
        context.setSortFields(toArray(EVENT_UTC_START_DATE_TIME));
        return context;
    }
}
