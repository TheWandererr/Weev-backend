package com.pivo.weev.backend.domain.persistance.jpa.specification.builder;

import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SimpleSpecifications.empty;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.EVENT_STATUS;

import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.specification.SpecificationBuilder;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class EventsSpecificationBuilder {

    public static Specification<EventJpa> buildEventsSearchSpecification(SearchParams searchParams) {
        if (searchParams.isOnModeration()) {
            return buildModerationEventsSpecification();
        }
        return empty();
    }

    private static Specification<EventJpa> buildModerationEventsSpecification() {
        SpecificationBuilder<EventJpa> specificationBuilder = new SpecificationBuilder<>();
        return specificationBuilder.andEqual(EVENT_STATUS, ON_MODERATION.name(), 0, String.class)
                                   .build();
    }
}
