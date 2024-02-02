package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.meet.MeetJoinRequest;
import com.pivo.weev.backend.rest.model.meet.MeetJoinRequestRest;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(uses = {UserSnapshotRestMapper.class, MeetSnapshotRestMapper.class})
public interface MeetJoinRequestRestMapper {

    MeetJoinRequestRest map(MeetJoinRequest source);

    List<MeetJoinRequestRest> map(List<MeetJoinRequest> source);
}
