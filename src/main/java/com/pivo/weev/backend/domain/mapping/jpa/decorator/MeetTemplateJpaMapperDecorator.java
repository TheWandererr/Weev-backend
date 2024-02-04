package com.pivo.weev.backend.domain.mapping.jpa.decorator;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.MeetTemplateJpaMapper;
import com.pivo.weev.backend.domain.mapping.jpa.RestrictionsJpaMapper;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class MeetTemplateJpaMapperDecorator implements MeetTemplateJpaMapper {

    private final MeetTemplateJpaMapper delegate;

    @Override
    public MeetTemplateJpa map(MeetJpa source) {
        MeetTemplateJpa mapped = delegate.map(source);
        RestrictionsJpa restrictions = new RestrictionsJpa();
        getMapper(RestrictionsJpaMapper.class).map(source.getRestrictions(), restrictions);
        mapped.setRestrictions(restrictions);
       /* LocationJpa location = new LocationJpa();
        getMapper(LocationJpaMapper.class).map(source.getLocation(), location);
        mapped.setLocation(location);*/
        return mapped;
    }
}
