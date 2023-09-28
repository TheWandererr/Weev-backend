package com.pivo.weev.backend.domain.persistance.jpa.specification;

import static com.pivo.weev.backend.common.utils.ArrayUtils.toArray;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.CriteriaParamsBuilder.buildCriteriaParams;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.ExpressionBuilder.getExpression;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.CriteriaFunctions.DISTANCE_3D;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_LOCATION;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaParams;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;

public class EventRadiusSpecification implements Specification<EventJpa> {

    private final Point point;
    private final double radius;
    private final CriteriaParams<Point> radiusParams;

    public EventRadiusSpecification(Point point, double radius) {
        this.point = point;
        this.radius = radius;
        this.radiusParams = buildCriteriaParams(EVENT_LOCATION, 1, Point.class);
    }

    @Override
    public Predicate toPredicate(Root<EventJpa> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Expression<Point> locationExpression = getExpression(radiusParams, root);
        var functionArgs = toArray(
                locationExpression,
                builder.literal(point),
                builder.literal(radius * 1000),
                builder.literal(true));
        return builder.equal(builder.function(DISTANCE_3D, Boolean.class, functionArgs), true);
    }
}
