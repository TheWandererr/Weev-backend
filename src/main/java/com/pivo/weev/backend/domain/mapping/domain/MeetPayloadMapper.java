package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.payload.MeetPayload;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import org.mapstruct.Mapper;

@Mapper
public interface MeetPayloadMapper {

    MeetPayload map(MeetJpa meetJpa);
}
