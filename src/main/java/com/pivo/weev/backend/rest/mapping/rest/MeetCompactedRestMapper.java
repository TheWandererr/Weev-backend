package com.pivo.weev.backend.rest.mapping.rest;

import static com.pivo.weev.backend.rest.utils.MeetViewUtils.hasPrivateData;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.rest.model.meet.LocationRest;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {ImageRestMapper.class, EntryFeeRestMapper.class, RestrictionsRestMapper.class, UserSnapshotRestMapper.class})
public interface MeetCompactedRestMapper {

    LocationRestMapper LOCATION_REST_MAPPER = getMapper(LocationRestMapper.class);

    @Mapping(target = "membersCount", expression = "java(source.getMembers().size())")
    @Mapping(target = "ended", expression = "java(source.isEnded())")
    @Mapping(target = "started", expression = "java(source.isStarted())")
    @Mapping(target = "location", ignore = true)
    MeetCompactedRest map(Meet source);

    List<MeetCompactedRest> mapCompacted(List<Meet> source);

    @AfterMapping
    default void mapPrivateData(Meet source, @MappingTarget MeetCompactedRest target) {
        boolean hasHiddenData = hasPrivateData(source);
        LocationRest restLocation = hasHiddenData
                ? LOCATION_REST_MAPPER.mapPrivate(source.getLocation())
                : LOCATION_REST_MAPPER.map(source.getLocation());
        target.setLocation(restLocation);
    }
}
