package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.utils.AuthUtils;
import com.pivo.weev.backend.rest.mapping.rest.decorator.MeetDetailedRestMapperDecorator;
import com.pivo.weev.backend.rest.model.meet.MeetDetailedRest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ImageRestMapper.class, RestrictionsRestMapper.class}, imports = AuthUtils.class)
@DecoratedWith(MeetDetailedRestMapperDecorator.class)
public interface MeetDetailedRestMapper extends MeetCompactedRestMapper {

    @Mapping(target = "location", ignore = true)
    MeetDetailedRest map(Meet source);
}
