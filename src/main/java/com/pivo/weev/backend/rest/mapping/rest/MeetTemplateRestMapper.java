package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.rest.model.meet.MeetTemplateRest;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(uses = {ImageRestMapper.class, RestrictionsRestMapper.class, UserSnapshotRestMapper.class, LocationRestMapper.class})
public interface MeetTemplateRestMapper {

    MeetTemplateRest map(Meet source);

    List<MeetTemplateRest> map(List<Meet> source);
}
