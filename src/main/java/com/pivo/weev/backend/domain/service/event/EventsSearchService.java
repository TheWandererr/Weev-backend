package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.common.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.EventSpecificationBuilder.buildEventsSearchSpecification;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.PageableUtils.build;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.EventMapper;
import com.pivo.weev.backend.domain.mapping.domain.MapPointMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.common.MapPointCluster;
import com.pivo.weev.backend.domain.model.event.Event;
import com.pivo.weev.backend.domain.model.event.SearchParams;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import com.pivo.weev.backend.domain.service.clusterization.MapClusterizationService;
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

    private final MapClusterizationService mapClusterizationService;

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
        return eventRepository.findById(id)
                              .map(event -> getMapper(EventMapper.class).map(event))
                              .orElse(null);
    }

    @Transactional
    public Page<MapPointCluster> searchMapPointClusters(SearchParams searchParams) {
        Page<MapPoint> mapPoints = search(searchParams, list -> {
            List<LocationJpa> locations = mapToList(list, EventJpa::getLocation);
            return getMapper(MapPointMapper.class).map(locations);
        });
        List<MapPointCluster> clusters = mapClusterizationService.process(mapPoints.getContent(), searchParams.getZoom());
        return new PageImpl<>(clusters, mapPoints.getPageable(), mapPoints.getTotalElements());
    }
}
