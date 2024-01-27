package com.pivo.weev.backend.domain.persistance.jpa.specification.builder;

import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.DECLINED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.DELETED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.contains;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.containsIgnoreCase;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.empty;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.equal;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.in;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.notEqual;
import static com.pivo.weev.backend.domain.persistance.utils.CustomGeometryFactory.createPoint;
import static com.pivo.weev.backend.domain.persistance.utils.SpecificationUtils.fieldPathFrom;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.Radius;
import com.pivo.weev.backend.domain.model.event.Restrictions;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.model.event.SearchParams.FieldsCriteria;
import com.pivo.weev.backend.domain.model.event.SearchParams.MapCriteria;
import com.pivo.weev.backend.domain.model.event.SearchParams.VisibilityCriteria;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable_;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.CategoryJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.RestrictionsJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.SubcategoryJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications;
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
        VisibilityCriteria visibilityCriteria = searchParams.getVisibilityCriteria();
        if (searchParams.hasAuthorId()) {
            return specificationBuilder.andAll(collectAuthorsSpecification(searchParams))
                                       .build();
        }
        if (visibilityCriteria.isOnModeration()) {
            return specificationBuilder.and(buildModerationSpecification())
                                       .build();
        }
        return specificationBuilder
                .andAll(collectFieldsSpecifications(searchParams))
                .and(buildStatusSpecification(searchParams))
                .and(buildRadiusSpecification(searchParams))
                .and(buildSortSpecification(searchParams))
                .and(buildGeoHashSpecification(searchParams))
                .and(buildRestrictionsSpecification(searchParams))
                .build();
    }

    private Specification<EventJpa> buildModerationSpecification() {
        return equal(EventJpa_.status, ON_MODERATION.name(), String.class);
    }

    private List<Specification<EventJpa>> collectAuthorsSpecification(SearchParams searchParams) {
        return of(
                buildStatusSpecification(searchParams),
                equal(fieldPathFrom(EventJpa_.creator, SequencedPersistable_.id), searchParams.getAuthorId(), Long.class),
                SimpleSpecifications.isNull(fieldPathFrom(EventJpa_.updatableTarget, SequencedPersistable_.id), Long.class)
        );
    }

    private List<Specification<EventJpa>> collectFieldsSpecifications(SearchParams searchParams) {
        if (searchParams.hasFieldsCriteria()) {
            FieldsCriteria fieldsCriteria = searchParams.getFieldsCriteria();
            return of(
                    containsIgnoreCase(EventJpa_.header, fieldsCriteria.getHeader()),
                    equal(fieldPathFrom(EventJpa_.category, CategoryJpa_.name), fieldsCriteria.getCategory(), 1, String.class),
                    equal(fieldPathFrom(EventJpa_.subcategory, SubcategoryJpa_.name), fieldsCriteria.getSubcategory(), 1, String.class));
        }
        return emptyList();
    }

    private Specification<EventJpa> buildStatusSpecification(SearchParams searchParams) {
        VisibilityCriteria visibilityCriteria = searchParams.getVisibilityCriteria();
        if (visibilityCriteria.isPublished()) {
            return in(EventJpa_.status, of(CONFIRMED, HAS_MODERATION_INSTANCE));
        }
        if (visibilityCriteria.isOnModeration()) {
            return buildModerationSpecification();
        }
        if (visibilityCriteria.isCanceled()) {
            return equal(EventJpa_.status, CANCELED.name(), String.class);
        }
        if (visibilityCriteria.isDeclined()) {
            return equal(EventJpa_.status, DECLINED.name(), String.class);
        }
        return notEqual(EventJpa_.status, DELETED.name(), String.class);
    }

    private Specification<EventJpa> buildRadiusSpecification(SearchParams searchParams) {
        MapCriteria mapCriteria = searchParams.getMapCriteria();
        if (isNull(mapCriteria) || !mapCriteria.hasRadius()) {
            return empty();
        }
        Radius radius = mapCriteria.getRadius();
        MapPoint mapPoint = radius.getPoint();
        Point point = createPoint(mapPoint.getLng(), mapPoint.getLtd());
        return new EventRadiusSpecification(point, radius.getValue());
    }

    private Specification<EventJpa> buildSortSpecification(SearchParams searchParams) {
        return searchParams.hasSortFields() ? empty() : SORT_SPECIFICATION;
    }

    private Specification<EventJpa> buildGeoHashSpecification(SearchParams searchParams) {
        MapCriteria mapCriteria = searchParams.getMapCriteria();
        if (isNull(mapCriteria) || !mapCriteria.hasGeoHash()) {
            return empty();
        }
        return contains(fieldPathFrom(EventJpa_.location, LocationJpa_.hash), mapCriteria.getGeoHash(), 1);
    }

    private Specification<EventJpa> buildRestrictionsSpecification(SearchParams searchParams) {
        if (!searchParams.hasRestrictions()) {
            return empty();
        }
        Restrictions restrictions = searchParams.getFieldsCriteria().getRestrictions();
        return equal(fieldPathFrom(EventJpa_.restrictions, RestrictionsJpa_.availability), restrictions.getAvailability().name(), String.class);
    }

}
