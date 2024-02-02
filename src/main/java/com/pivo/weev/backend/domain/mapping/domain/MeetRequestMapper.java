package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.meet.MeetJoinRequest;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJoinRequestJpa;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {MeetMapper.class, UserMapper.class})
public interface MeetRequestMapper {

    @Mapping(target = "joiner", source = "user")
    @Mapping(target = "meet", source = "meet")
    MeetJoinRequest map(MeetJoinRequestJpa source);

    List<MeetJoinRequest> map(List<MeetJoinRequestJpa> source);
}
