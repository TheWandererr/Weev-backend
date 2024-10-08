package com.pivo.weev.backend.domain.persistance.jpa.specification.template;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.CriteriaParamsBuilder.buildCriteriaParams;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.ExpressionBuilder.getExpression;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.CriteriaFunctions.DISTANCE_3D;
import static com.pivo.weev.backend.domain.persistance.utils.SpecificationUtils.fieldPathFrom;
import static com.pivo.weev.backend.utils.ArrayUtils.toArray;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaParams;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.Specification;

public class MeetRadiusSpecification implements Specification<MeetJpa> {

    private final Point point;
    private final double radius;
    private final CriteriaParams<Point> pointParams;

    public MeetRadiusSpecification(Point point, double radius) {
        this.point = point;
        this.radius = radius;
        this.pointParams = buildCriteriaParams(fieldPathFrom(MeetJpa_.location, LocationJpa_.point), 1, Point.class);
    }

    @Override
    public Predicate toPredicate(Root<MeetJpa> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Expression<Point> locationExpression = getExpression(pointParams, root);
        var functionArgs = toArray(
                locationExpression,
                builder.literal(point),
                builder.literal(radius * 1000),
                builder.literal(true));
        return builder.equal(builder.function(DISTANCE_3D, Boolean.class, functionArgs), true);
    }
}
