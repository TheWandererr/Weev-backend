package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.mapping.domain.decorator.MeetMapperDecorator;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import java.util.List;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@DecoratedWith(MeetMapperDecorator.class)
@Mapper(uses = {CategoryMapper.class, SubcategoryMapper.class, UserMapper.class, LocationMapper.class, EntryFeeMapper.class, ImageMapper.class, RestrictionsMapper.class})
public interface MeetMapper {

    Meet map(MeetJpa source);

    List<Meet> mapMeets(List<MeetJpa> source);

    List<Meet> mapTemplates(List<MeetTemplateJpa> content);

    Meet map(MeetTemplateJpa content);

    default String mapStatus(MeetStatus status) {
        return status.name();
    }
}
