package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.EventsSpecificationBuilder.buildEventsSearchSpecification;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.PageableUtils.build;
import static com.pivo.weev.backend.domain.utils.Constants.PageableParams.EVENTS_PER_PAGE;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.EventMapper;
import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsSearchService {

    private final EventRepositoryWrapper eventRepository;

    @Transactional
    public Page<Event> search(SearchParams searchParams) {
        Pageable pageable = build(searchParams.getPage(), EVENTS_PER_PAGE, searchParams.getSortFields());
        Specification<EventJpa> specification = buildEventsSearchSpecification(searchParams);
        Page<EventJpa> jpaPage = eventRepository.findAll(specification, pageable);
        List<Event> content = getMapper(EventMapper.class).map(jpaPage.getContent());
        return new PageImpl<>(content, jpaPage.getPageable(), jpaPage.getTotalElements());
    }
}
