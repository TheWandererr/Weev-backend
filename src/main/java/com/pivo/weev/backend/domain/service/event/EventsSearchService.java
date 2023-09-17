package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.EventsSpecificationBuilder.buildEventsSearchSpecification;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.PageableUtils.build;
import static com.pivo.weev.backend.domain.utils.Constants.PageableParams.EVENTS_PER_PAGE;

import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsSearchService {

    private final EventRepositoryWrapper eventRepository;

    public Page<EventJpa> search(SearchParams searchParams) {
        Pageable pageable = build(searchParams.getPage(), EVENTS_PER_PAGE, searchParams.getSortFields());
        Specification<EventJpa> specification = buildEventsSearchSpecification(searchParams);
        return eventRepository.findAll(specification, pageable);
    }
}
