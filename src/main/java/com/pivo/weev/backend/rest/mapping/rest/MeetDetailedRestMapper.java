package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.utils.AuthUtils;
import com.pivo.weev.backend.rest.model.meet.MeetDetailedRest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ImageRestMapper.class, EntryFeeRestMapper.class, RestrictionsRestMapper.class}, imports = AuthUtils.class)
public interface MeetDetailedRestMapper extends MeetCompactedRestMapper {

    @Mapping(target = "membersCount", expression = "java(source.getMembers().size())")
    @Mapping(target = "ended", expression = "java(source.isEnded())")
    @Mapping(target = "started", expression = "java(source.isStarted())")
    @Mapping(target = "member", expression = "java(source.hasMember(AuthUtils.getNullableUserId()))")
    @Mapping(target = "location", ignore = true)
    MeetDetailedRest map(Meet source);
}
