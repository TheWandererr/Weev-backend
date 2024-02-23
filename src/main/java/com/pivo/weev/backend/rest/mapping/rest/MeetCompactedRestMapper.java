package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.rest.mapping.rest.decorator.MeetCompactedRestMapperDecorator;
import com.pivo.weev.backend.rest.model.meet.MeetCompactedRest;
import java.util.List;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@DecoratedWith(MeetCompactedRestMapperDecorator.class)
@Mapper(uses = {ImageRestMapper.class, RestrictionsRestMapper.class, UserSnapshotRestMapper.class, LocationRestMapper.class})
public interface MeetCompactedRestMapper {

    MeetCompactedRest map(Meet source);

    List<MeetCompactedRest> map(List<Meet> source);
}
