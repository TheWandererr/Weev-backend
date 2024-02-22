package com.pivo.weev.backend.domain.model.meet;

import static com.pivo.weev.backend.utils.ArrayUtils.isEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import ch.hsr.geohash.BoundingBox;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class SearchParams {

    private Pageable pageable;
    private MapCriteria mapCriteria;
    private VisibilityCriteria visibilityCriteria = new VisibilityCriteria();
    private FieldsCriteria fieldsCriteria;
    private Long authorId;

    public boolean hasMapCriteria() {
        return nonNull(mapCriteria);
    }

    public boolean hasVisibilityCriteria() {
        return nonNull(visibilityCriteria);
    }

    public boolean hasFieldsCriteria() {
        return nonNull(fieldsCriteria);
    }

    public int getZoom() {
        return hasMapCriteria() ? getMapCriteria().getZoom() : 0;
    }

    public boolean hasRestrictions() {
        return hasFieldsCriteria() && getFieldsCriteria().hasRestrictions();
    }

    public boolean hasAuthorId() {
        return nonNull(authorId);
    }

    public boolean hasPageable() {
        return nonNull(pageable);
    }

    public boolean hasSort() {
        if (hasPageable()) {
            Sort sort = getPageable().getSort();
            return sort.isSorted();
        }
        return false;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class PageCriteria {

        private int page;
        private int pageSize;
        private String[] sortFields;

        public PageCriteria(int page, int pageSize) {
            this.page = page;
            this.pageSize = pageSize;
        }

        public String[] getSortFields() {
            if (isNull(sortFields)) {
                sortFields = new String[]{};
            }
            return sortFields;
        }

        public boolean hasSortFields() {
            return !isEmpty(getSortFields());
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class MapCriteria {

        private Radius radius;
        private int zoom;
        private String geoHash;
        private BoundingBox bbox;

        public boolean hasRadius() {
            return nonNull(radius);
        }

        public boolean hasGeoHash() {
            return isNotBlank(geoHash);
        }

        public boolean hasBbox() {
            return nonNull(bbox);
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class VisibilityCriteria {

        private boolean onModeration;
        private boolean published;
        private boolean canceled;
        private boolean declined;

        public VisibilityCriteria() {
            this.published = true;
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class FieldsCriteria {

        private String header;
        private String category;
        private String subcategory;
        private Restrictions restrictions;

        public boolean hasRestrictions() {
            return nonNull(restrictions);
        }
    }
}
