package com.pivo.weev.backend.domain.persistance.jpa.specification.builder;

import static com.google.common.collect.Lists.newArrayList;
import static com.pivo.weev.backend.domain.model.meet.Restrictions.Availability.PRIVATE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.DECLINED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.DELETED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.contains;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.containsIgnoreCase;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.empty;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.equal;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.in;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.notEqual;
import static com.pivo.weev.backend.domain.persistance.utils.CustomGeometryFactory.createPoint;
import static com.pivo.weev.backend.domain.persistance.utils.SpecificationUtils.fieldPathFrom;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.meet.Radius;
import com.pivo.weev.backend.domain.model.meet.Restrictions.Availability;
import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.domain.model.meet.SearchParams.FieldsCriteria;
import com.pivo.weev.backend.domain.model.meet.SearchParams.MapCriteria;
import com.pivo.weev.backend.domain.model.meet.SearchParams.VisibilityCriteria;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable_;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.CategoryJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.SubcategoryJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SpecificationBuilder;
import com.pivo.weev.backend.domain.persistance.jpa.specification.template.MeetBBoxSpecification;
import com.pivo.weev.backend.domain.persistance.jpa.specification.template.MeetRadiusSpecification;
import com.pivo.weev.backend.domain.persistance.jpa.specification.template.MeetsSortSpecification;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class MeetSpecificationBuilder {

    private static final Specification<MeetJpa> SORT_SPECIFICATION = new MeetsSortSpecification();
    private static final Specification<MeetJpa> PUBLIC_AVAILABILITY_SPECIFICATION = notEqual(
            fieldPathFrom(MeetJpa_.restrictions, RestrictionsJpa_.availability),
            PRIVATE.name(),
            String.class
    );

    public static Specification<MeetJpa> buildMeetsSearchSpecification(SearchParams searchParams) {
        SpecificationBuilder<MeetJpa> specificationBuilder = new SpecificationBuilder<>();
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
                .and(buildBoundingBoxSpecification(searchParams))
                .and(buildRestrictionsSpecification(searchParams))
                .build();
    }

    private Specification<MeetJpa> buildModerationSpecification() {
        return equal(MeetJpa_.status, ON_MODERATION.name(), String.class);
    }

    private List<Specification<MeetJpa>> collectAuthorsSpecification(SearchParams searchParams) {
        List<Specification<MeetJpa>> specifications = newArrayList(
                buildStatusSpecification(searchParams),
                equal(fieldPathFrom(MeetJpa_.creator, SequencedPersistable_.id), searchParams.getAuthorId(), Long.class),
                SimpleSpecifications.isNull(fieldPathFrom(MeetJpa_.updatableTarget, SequencedPersistable_.id), Long.class)
        );
        if (!Objects.equals(searchParams.getAuthorId(), getUserId())) {
            specifications.add(PUBLIC_AVAILABILITY_SPECIFICATION);
        }
        return specifications;
    }

    private List<Specification<MeetJpa>> collectFieldsSpecifications(SearchParams searchParams) {
        if (searchParams.hasFieldsCriteria()) {
            FieldsCriteria fieldsCriteria = searchParams.getFieldsCriteria();
            return of(
                    containsIgnoreCase(MeetJpa_.header, fieldsCriteria.getHeader()),
                    equal(fieldPathFrom(MeetJpa_.category, CategoryJpa_.name), fieldsCriteria.getCategory(), 1, String.class),
                    equal(fieldPathFrom(MeetJpa_.subcategory, SubcategoryJpa_.name), fieldsCriteria.getSubcategory(), 1, String.class));
        }
        return emptyList();
    }

    private Specification<MeetJpa> buildStatusSpecification(SearchParams searchParams) {
        VisibilityCriteria visibilityCriteria = searchParams.getVisibilityCriteria();
        if (visibilityCriteria.isPublished()) {
            return in(MeetJpa_.status, of(CONFIRMED, HAS_MODERATION_INSTANCE));
        }
        if (visibilityCriteria.isOnModeration()) {
            return buildModerationSpecification();
        }
        if (visibilityCriteria.isCanceled()) {
            return equal(MeetJpa_.status, CANCELED.name(), String.class);
        }
        if (visibilityCriteria.isDeclined()) {
            return equal(MeetJpa_.status, DECLINED.name(), String.class);
        }
        return notEqual(MeetJpa_.status, DELETED.name(), String.class);
    }

    private Specification<MeetJpa> buildRadiusSpecification(SearchParams searchParams) {
        MapCriteria mapCriteria = searchParams.getMapCriteria();
        if (isNull(mapCriteria) || !mapCriteria.hasRadius()) {
            return empty();
        }
        Radius radius = mapCriteria.getRadius();
        MapPoint mapPoint = radius.getPoint();
        Point point = createPoint(mapPoint.getLng(), mapPoint.getLtd());
        return new MeetRadiusSpecification(point, radius.getValue());
    }

    private Specification<MeetJpa> buildSortSpecification(SearchParams searchParams) {
        return searchParams.hasSortFields() ? empty() : SORT_SPECIFICATION;
    }

    private Specification<MeetJpa> buildGeoHashSpecification(SearchParams searchParams) {
        MapCriteria mapCriteria = searchParams.getMapCriteria();
        if (isNull(mapCriteria) || !mapCriteria.hasGeoHash()) {
            return empty();
        }
        return contains(fieldPathFrom(MeetJpa_.location, LocationJpa_.geoHash), mapCriteria.getGeoHash(), 1);
    }

    private Specification<MeetJpa> buildBoundingBoxSpecification(SearchParams searchParams) {
        MapCriteria mapCriteria = searchParams.getMapCriteria();
        if (isNull(mapCriteria) || !mapCriteria.hasBbox()) {
            return empty();
        }
        return new MeetBBoxSpecification(mapCriteria.getBbox());
    }

    private Specification<MeetJpa> buildRestrictionsSpecification(SearchParams searchParams) {
        if (!searchParams.hasRestrictions()) {
            return PUBLIC_AVAILABILITY_SPECIFICATION;
        }
        Availability availability = searchParams.getFieldsCriteria()
                                                .getRestrictions()
                                                .getAvailability();
        return isNull(availability) || availability == PRIVATE
                ? PUBLIC_AVAILABILITY_SPECIFICATION
                : equal(fieldPathFrom(MeetJpa_.restrictions, RestrictionsJpa_.availability), availability.name(), String.class);
    }
}
