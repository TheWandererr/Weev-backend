package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.rest.mapping.rest.decorator.MeetCompactedRestMapperDecorator;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import java.util.List;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@DecoratedWith(MeetCompactedRestMapperDecorator.class)
@Mapper(uses = {ImageRestMapper.class, EntryFeeRestMapper.class, RestrictionsRestMapper.class, UserSnapshotRestMapper.class, LocationRestMapper.class})
public interface MeetCompactedRestMapper {

    @Mapping(target = "membersCount", expression = "java(source.getMembers().size())")
    @Mapping(target = "ended", expression = "java(source.isEnded())")
    @Mapping(target = "started", expression = "java(source.isStarted())")
    MeetCompactedRest map(Meet source);

    List<MeetCompactedRest> map(List<Meet> source);
}
