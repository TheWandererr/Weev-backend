package com.pivo.weev.backend.domain.persistance.jpa.specification.template;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.CriteriaParamsBuilder.buildCriteriaParams;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.ExpressionBuilder.getExpression;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.CriteriaFunctions.DATE_PART;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.CriteriaLiterals.EPOCH;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa_;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaParams;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public class EventsSortSpecification implements Specification<EventJpa> {

    private final CriteriaParams<LocalDateTime> startDateTimeCriteriaParams;
    private final CriteriaParams<LocalDateTime> endDateTimeCriteriaParams;

    public EventsSortSpecification() {
        this.startDateTimeCriteriaParams = buildCriteriaParams(EventJpa_.utcStartDateTime, 0, LocalDateTime.class);
        this.endDateTimeCriteriaParams = buildCriteriaParams(EventJpa_.utcEndDateTime, 0, LocalDateTime.class);
    }

    @Override
    public Predicate toPredicate(Root<EventJpa> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Expression<Long> epochStartDateTimeExpression = builder.function(DATE_PART, Long.class, builder.literal(EPOCH), getExpression(startDateTimeCriteriaParams, root));
        Long currentEpoch = Instant.now().getEpochSecond();

        Expression<Long> epochEndDateTimeExpression = builder.function(DATE_PART, Long.class, builder.literal(EPOCH), getExpression(endDateTimeCriteriaParams, root));

        Expression<Long> weight = builder.abs(builder.diff(epochStartDateTimeExpression, currentEpoch));
        Expression<Long> past = builder.diff(epochEndDateTimeExpression, currentEpoch);

        query.orderBy(builder.desc(past), builder.asc(weight));
        return null;
    }
}
