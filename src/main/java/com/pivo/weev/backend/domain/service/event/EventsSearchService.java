package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.EventsSpecificationBuilder.buildEventsSearchSpecification;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.PageableUtils.build;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.EventMapPointMapper;
import com.pivo.weev.backend.domain.mapping.domain.EventMapper;
import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.EventMapPoint;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.function.Function;
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
        return search(searchParams, list -> getMapper(EventMapper.class).map(list));
    }

    private <T> Page<T> search(SearchParams searchParams, Function<List<EventJpa>, List<T>> mapper) {
        Pageable pageable = build(searchParams.getPage(), searchParams.getPageSize(), searchParams.getSortFields());
        Specification<EventJpa> specification = buildEventsSearchSpecification(searchParams);
        Page<EventJpa> jpaPage = eventRepository.findAll(specification, pageable);
        List<T> content = mapper.apply(jpaPage.getContent());
        return new PageImpl<>(content, jpaPage.getPageable(), jpaPage.getTotalElements());
    }

    @Transactional
    public Event search(Long id) {
        return eventRepository.find(id)
                              .map(event -> getMapper(EventMapper.class).map(event))
                              .orElse(null);
    }

    @Transactional
    public Page<EventMapPoint> searchMapPoints(SearchParams searchParams) {
        return search(searchParams, list -> getMapper(EventMapPointMapper.class).map(list));
    }
}
