package com.pivo.weev.backend.domain.model.event;

import static java.util.Objects.isNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchParams {

    private int page;
    private String[] sortFields;

    private boolean onModeration;

    public String[] getSortFields() {
        if (isNull(sortFields)) {
            return new String[]{};
        }
        return sortFields;
    }
}
