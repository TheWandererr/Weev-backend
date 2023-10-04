package com.pivo.weev.backend.domain.persistance.jpa.specification.builder;

import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.containsIgnoreCase;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.empty;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.equal;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.in;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_CATEGORY;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_HEADER;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_STATUS;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_SUBCATEGORY;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.CustomGeometryFactory.createPoint;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.Radius;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.specification.EventRadiusSpecification;
import com.pivo.weev.backend.domain.persistance.jpa.specification.EventsSortSpecification;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SpecificationBuilder;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class EventsSpecificationBuilder {

    private static final Specification<EventJpa> SORT_SPECIFICATION = new EventsSortSpecification();

    public static Specification<EventJpa> buildEventsSearchSpecification(SearchParams searchParams) {
        SpecificationBuilder<EventJpa> specificationBuilder = new SpecificationBuilder<>();
        if (searchParams.isOnModeration()) {
            return specificationBuilder.andEqual(EVENT_STATUS, ON_MODERATION.name(), String.class)
                                       .build();
        }
        return specificationBuilder
                .andAll(collectTextSpecifications(searchParams))
                .and(buildStatusSpecification(searchParams))
                .and(buildRadiusSpecification(searchParams))
                .and(buildSortSpecification(searchParams))
                .build();
    }

    private List<Specification<EventJpa>> collectTextSpecifications(SearchParams searchParams) {
        return List.of(containsIgnoreCase(EVENT_HEADER, searchParams.getHeader()),
                       equal(EVENT_CATEGORY, searchParams.getCategory(), String.class),
                       equal(EVENT_SUBCATEGORY, searchParams.getSubcategory(), String.class));
    }

    private Specification<EventJpa> buildStatusSpecification(SearchParams searchParams) {
        if (searchParams.isPublished()) {
            return in(EVENT_STATUS, List.of(CONFIRMED, HAS_MODERATION_INSTANCE));
        }
        return empty();
    }

    private Specification<EventJpa> buildRadiusSpecification(SearchParams searchParams) {
        if (!searchParams.hasRadius()) {
            return empty();
        }
        Radius radius = searchParams.getRadius();
        MapPoint mapPoint = radius.getPoint();
        Point point = createPoint(mapPoint.getLng(), mapPoint.getLtd());
        return new EventRadiusSpecification(point, radius.getValue());
    }

    private Specification<EventJpa> buildSortSpecification(SearchParams searchParams) {
        return searchParams.hasSortFields() ? empty() : SORT_SPECIFICATION;
    }
}
