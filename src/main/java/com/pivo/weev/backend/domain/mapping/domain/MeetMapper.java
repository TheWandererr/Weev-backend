package com.pivo.weev.backend.domain.mapping.domain;

import static com.pivo.weev.backend.utils.CollectionUtils.mapToSet;

import com.pivo.weev.backend.domain.mapping.domain.decorator.MeetMapperDecorator;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.List;
import java.util.Set;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@DecoratedWith(MeetMapperDecorator.class)
@Mapper(uses = {CategoryMapper.class, SubcategoryMapper.class, UserMapper.class, LocationMapper.class, ImageMapper.class, RestrictionsMapper.class})
public interface MeetMapper {

    @Mapping(target = "members", qualifiedByName = "mapMembers")
    Meet map(MeetJpa source);

    List<Meet> mapMeets(List<MeetJpa> source);

    List<Meet> mapTemplates(List<MeetTemplateJpa> content);

    Meet map(MeetTemplateJpa content);

    default String mapStatus(MeetStatus status) {
        return status.name();
    }

    @Named("mapMembers")
    default Set<Long> mapMembers(Set<UserJpa> source) {
        return mapToSet(source, UserJpa::getId);
    }
}
