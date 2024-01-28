package com.pivo.weev.backend.domain.persistance.jpa.specification.template;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.CriteriaParamsBuilder.buildCriteriaParams;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.ExpressionBuilder.getExpression;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.CriteriaFunctions.ST_CONTAINS;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.CriteriaFunctions.ST_MAKE_ENVELOPE;
import static com.pivo.weev.backend.domain.persistance.utils.SpecificationUtils.fieldPathFrom;
import static com.pivo.weev.backend.utils.ArrayUtils.toArray;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.WGS84Point;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaParams;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;

public class EventBBoxSpecification implements Specification<EventJpa> {

    private final BoundingBox boundingBox;
    private final CriteriaParams<Point> pointParams;

    public EventBBoxSpecification(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
        this.pointParams = buildCriteriaParams(fieldPathFrom(EventJpa_.location, LocationJpa_.point), 1, Point.class);
    }

    @Override
    public Predicate toPredicate(Root<EventJpa> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Expression<Point> locationExpression = getExpression(pointParams, root);
        WGS84Point northWestCorner = boundingBox.getNorthWestCorner();
        WGS84Point southEastCorner = boundingBox.getSouthEastCorner();
        var stMakeEnvelopeArgs = toArray(
                builder.literal(southEastCorner.getLatitude()),
                builder.literal(northWestCorner.getLongitude()),
                builder.literal(northWestCorner.getLatitude()),
                builder.literal(southEastCorner.getLongitude()),
                builder.literal(4326)
        );
        Expression<Object> stMakeEnvelope = builder.function(ST_MAKE_ENVELOPE, Object.class, stMakeEnvelopeArgs);
        Expression<Boolean> stContains = builder.function(ST_CONTAINS, Boolean.class, stMakeEnvelope, locationExpression);
        return builder.equal(stContains, Boolean.TRUE);
    }
}
