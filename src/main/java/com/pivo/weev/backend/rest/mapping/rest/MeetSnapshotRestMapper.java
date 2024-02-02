package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.rest.model.meet.MeetSnapshotRest;
import org.mapstruct.Mapper;

@Mapper(uses = {ImageRestMapper.class})
public interface MeetSnapshotRestMapper {

    MeetSnapshotRest map(Meet source);
}
