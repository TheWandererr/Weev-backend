package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.mapping.domain.decorator.MeetMapperDecorator;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus;
import java.util.List;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@DecoratedWith(MeetMapperDecorator.class)
@Mapper(uses = {CategoryMapper.class, SubcategoryMapper.class, UserMapper.class, LocationMapper.class, EntryFeeMapper.class, ImageMapper.class, RestrictionsMapper.class})
public interface MeetMapper {

    Meet map(MeetJpa source);

    List<Meet> map(List<MeetJpa> source);

    default String mapStatus(MeetStatus status) {
        return status.name();
    }
}
