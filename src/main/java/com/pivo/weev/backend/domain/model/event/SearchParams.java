package com.pivo.weev.backend.domain.model.event;

import static com.pivo.weev.backend.common.utils.ArrayUtils.isEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.common.utils.ArrayUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchParams {

    private int page;
    private String[] sortFields;

    private String header;
    private String category;
    private String subcategory;
    private Radius radius;

    private boolean onModeration;
    private boolean published;

    public String[] getSortFields() {
        if (isNull(sortFields)) {
            return new String[]{};
        }
        return sortFields;
    }

    public boolean hasRadius() {
        return nonNull(radius);
    }

    public boolean hasSortFields() {
        return !isEmpty(getSortFields());
    }
}
