package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.MeetSpecificationBuilder.buildMeetsSearchSpecification;
import static com.pivo.weev.backend.domain.persistance.utils.PageableUtils.build;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MapPointMapper;
import com.pivo.weev.backend.domain.mapping.domain.MeetMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.common.MapPointCluster;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.meet.SearchParams;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepository;
import com.pivo.weev.backend.domain.service.clusterization.MapClusterizationService;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetSearchService {

    private final MeetRepository meetRepository;

    private final MapClusterizationService mapClusterizationService;

    @Transactional
    public Page<Meet> search(SearchParams searchParams) {
        return search(searchParams, list -> getMapper(MeetMapper.class).mapMeets(list));
    }

    private <T> Page<T> search(SearchParams searchParams, Function<List<MeetJpa>, List<T>> mapper) {
        Pageable pageable = build(searchParams.getPage(), searchParams.getPageSize(), searchParams.getSortFields());
        Specification<MeetJpa> specification = buildMeetsSearchSpecification(searchParams);
        Page<MeetJpa> jpaPage = meetRepository.findAll(specification, pageable);
        List<T> content = mapper.apply(jpaPage.getContent());
        return new PageImpl<>(content, jpaPage.getPageable(), jpaPage.getTotalElements());
    }

    @Transactional
    public Meet search(Long id) {
        return meetRepository.findById(id)
                             .map(meet -> getMapper(MeetMapper.class).map(meet))
                             .orElse(null);
    }

    @Transactional
    public Optional<MeetJpa> searchJpa(Long id) {
        return meetRepository.findById(id);
    }

    @Transactional
    public Page<MapPointCluster> searchMapPointClusters(SearchParams searchParams) {
        Page<MapPoint> mapPoints = search(searchParams, list -> {
            List<LocationJpa> locations = mapToList(list, MeetJpa::getLocation);
            return getMapper(MapPointMapper.class).map(locations);
        });
        List<MapPointCluster> clusters = mapClusterizationService.createClusters(mapPoints.getContent(), searchParams.getZoom());
        return new PageImpl<>(clusters, mapPoints.getPageable(), mapPoints.getTotalElements());
    }
}
