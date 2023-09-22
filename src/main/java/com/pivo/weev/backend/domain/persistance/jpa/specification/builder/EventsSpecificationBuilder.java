package com.pivo.weev.backend.domain.persistance.jpa.specification.builder;

import static com.pivo.weev.backend.common.utils.ArrayUtils.toArray;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.CriteriaParamsBuilder.buildCriteriaParams;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.ExpressionBuilder.getExpression;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.containsIgnoreCase;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.empty;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.equal;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.in;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.CriteriaFunctions.DATE_PART;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.CriteriaFunctions.DISTANCE_3D;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.CriteriaLiterals.EPOCH;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_CATEGORY;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_HEADER;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_LOCATION;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_STATUS;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_SUBCATEGORY;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_UTC_END_DATE_TIME;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_UTC_START_DATE_TIME;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.CustomGeometryFactory.createPoint;

import com.pivo.weev.backend.domain.model.event.Radius;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaParams;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SpecificationBuilder;
import jakarta.persistence.criteria.Expression;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class EventsSpecificationBuilder {

    public static Specification<EventJpa> buildEventsSearchSpecification(SearchParams searchParams) {
        SpecificationBuilder<EventJpa> specificationBuilder = new SpecificationBuilder<>();
        if (searchParams.isOnModeration()) {
            return specificationBuilder.andEqual(EVENT_STATUS, ON_MODERATION.name(), String.class)
                                       .build();
        }
        return specificationBuilder
                .andAll(collectTextSpecifications(searchParams))
                .and(buildStatusSpecification(searchParams))
                .and(buildLocationSpecification(searchParams))
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

    private Specification<EventJpa> buildLocationSpecification(SearchParams searchParams) {
        if (!searchParams.hasRadius()) {
            return empty();
        }
        Radius radius = searchParams.getRadius();
        Point point = createPoint(radius.getLng(), radius.getLtd());
        CriteriaParams<Point> criteriaParams = buildCriteriaParams(EVENT_LOCATION, 1, Point.class);
        return (root, query, builder) -> {
            final Expression<Point> locationExpression = getExpression(criteriaParams, root);
            var functionArgs = toArray(
                    locationExpression,
                    builder.literal(point),
                    builder.literal(radius.getValue() * 1000),
                    builder.literal(true));
            return builder.equal(builder.function(DISTANCE_3D, Boolean.class, functionArgs), true);
        };
    }

    private Specification<EventJpa> buildSortSpecification(SearchParams searchParams) {
        if (searchParams.hasSortFields()) {
            return empty();
        }
        CriteriaParams<LocalDateTime> startDateTimeCriteriaParams = buildCriteriaParams(EVENT_UTC_START_DATE_TIME, 0, LocalDateTime.class);
        CriteriaParams<LocalDateTime> endDateTimeCriteriaParams = buildCriteriaParams(EVENT_UTC_END_DATE_TIME, 0, LocalDateTime.class);
        return (root, query, builder) -> {

            Expression<Long> epochStartDateTimeExpression = builder.function(DATE_PART, Long.class, builder.literal(EPOCH), getExpression(startDateTimeCriteriaParams, root));
            Long epochCurrentDateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            Expression<Long> epochEndDateTimeExpression = builder.function(DATE_PART, Long.class, builder.literal(EPOCH), getExpression(endDateTimeCriteriaParams, root));

            Expression<Long> weight = builder.abs(builder.diff(epochStartDateTimeExpression, epochCurrentDateTime));
            Expression<Long> past = builder.diff(epochEndDateTimeExpression, epochCurrentDateTime);

            query.orderBy(builder.desc(past), builder.asc(weight));
            return null;
        };
    }
}
