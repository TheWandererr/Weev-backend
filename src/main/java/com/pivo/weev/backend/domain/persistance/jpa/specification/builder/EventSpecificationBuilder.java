package com.pivo.weev.backend.domain.persistance.jpa.specification.builder;

import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.contains;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.containsIgnoreCase;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.empty;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.equal;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.in;
import static com.pivo.weev.backend.domain.persistance.utils.CustomGeometryFactory.createPoint;
import static com.pivo.weev.backend.domain.persistance.utils.SpecificationUtils.fieldPathFrom;
import static java.util.List.of;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.Radius;
import com.pivo.weev.backend.domain.model.event.Restrictions;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.RestrictionsJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SpecificationBuilder;
import com.pivo.weev.backend.domain.persistance.jpa.specification.template.EventRadiusSpecification;
import com.pivo.weev.backend.domain.persistance.jpa.specification.template.EventsSortSpecification;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class EventSpecificationBuilder {

    private static final Specification<EventJpa> SORT_SPECIFICATION = new EventsSortSpecification();

    public static Specification<EventJpa> buildEventsSearchSpecification(SearchParams searchParams) {
        SpecificationBuilder<EventJpa> specificationBuilder = new SpecificationBuilder<>();
        if (searchParams.isOnModeration()) {
            return specificationBuilder.andEqual(EventJpa_.status, ON_MODERATION.name(), String.class)
                                       .build();
        }
        return specificationBuilder
                .andAll(collectTextSpecifications(searchParams))
                .and(buildStatusSpecification(searchParams))
                .and(buildRadiusSpecification(searchParams))
                .and(buildSortSpecification(searchParams))
                .and(buildGeoHashSpecification(searchParams))
                .and(buildRestrictionsSpecification(searchParams))
                .build();
    }

    private List<Specification<EventJpa>> collectTextSpecifications(SearchParams searchParams) {
        return of(containsIgnoreCase(EventJpa_.header, searchParams.getHeader()),
                  equal(EventJpa_.category, searchParams.getCategory(), String.class),
                  equal(EventJpa_.subcategory, searchParams.getSubcategory(), String.class));
    }

    private Specification<EventJpa> buildStatusSpecification(SearchParams searchParams) {
        if (searchParams.isPublished()) {
            return in(EventJpa_.status, of(CONFIRMED, HAS_MODERATION_INSTANCE));
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

    private Specification<EventJpa> buildGeoHashSpecification(SearchParams searchParams) {
        if (!searchParams.hasGeoHash()) {
            return empty();
        }
        return contains(fieldPathFrom(EventJpa_.location, LocationJpa_.hash), searchParams.getGeoHash(), 1);
    }

    private Specification<EventJpa> buildRestrictionsSpecification(SearchParams searchParams) {
        if (!searchParams.hasRestrictions()) {
            return empty();
        }
        Restrictions restrictions = searchParams.getRestrictions();
        return equal(fieldPathFrom(EventJpa_.restrictions, RestrictionsJpa_.availability), restrictions.getAvailability().name(), String.class);
    }

}
