package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.mapping.jpa.decorator.MeetTemplateJpaMapperDecorator;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper
@DecoratedWith(MeetTemplateJpaMapperDecorator.class)
public interface MeetTemplateJpaMapper {

    MeetTemplateJpa map(MeetJpa source);
}
