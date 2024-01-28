package com.pivo.weev.backend.domain.model.event;

import static com.pivo.weev.backend.utils.ArrayUtils.isEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import ch.hsr.geohash.BoundingBox;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchParams {

    private PageCriteria pageCriteria = new PageCriteria();
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

    public Integer getPage() {
        return getPageCriteria().getPage();
    }

    public Integer getPageSize() {
        return getPageCriteria().getPageSize();
    }

    public String[] getSortFields() {
        return getPageCriteria().getSortFields();
    }

    public int getZoom() {
        return hasMapCriteria() ? getMapCriteria().getZoom() : 0;
    }

    public boolean hasSortFields() {
        return getPageCriteria().hasSortFields();
    }

    public boolean hasRestrictions() {
        return hasFieldsCriteria() && getFieldsCriteria().hasRestrictions();
    }

    public boolean hasAuthorId() {
        return nonNull(authorId);
    }

    @Getter
    @Setter
    public static class PageCriteria {

        private int page;
        private int pageSize;
        private String[] sortFields;

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
