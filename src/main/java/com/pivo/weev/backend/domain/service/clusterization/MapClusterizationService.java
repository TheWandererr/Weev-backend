package com.pivo.weev.backend.domain.service.clusterization;

import static com.pivo.weev.backend.common.utils.CollectionUtils.mapToList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MapPointMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.common.MapPointCluster;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapClusterizationService {

    public List<MapPointCluster> process(List<MapPoint> data, int zoom) {
        Map<String, List<MapPoint>> groupedPoints = data.stream().collect(groupingBy(point -> point.getGeoHashString().substring(0, zoom), toList()));
        return mapToList(groupedPoints.entrySet(), entry -> new MapPointCluster(createCentroid(entry.getKey()), entry.getValue()));
    }

    private MapPoint createCentroid(String hashString) {
        return getMapper(MapPointMapper.class).map(hashString);
    }
}
