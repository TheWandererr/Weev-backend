package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import org.mapstruct.Mapper;

@Mapper
public interface MeetTemplateJpaMapper {

    MeetTemplateJpa map(MeetJpa source);
}
