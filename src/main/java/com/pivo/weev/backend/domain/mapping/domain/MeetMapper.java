package com.pivo.weev.backend.domain.mapping.domain;

import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.DELETED;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(uses = {CategoryMapper.class, SubcategoryMapper.class, UserMapper.class, LocationMapper.class, EntryFeeMapper.class, ImageMapper.class, RestrictionsMapper.class})
public interface MeetMapper {

    @Named("map")
    default Meet map(MeetJpa source) {
        if (source.isDeleted()) {
            Meet meet = new Meet();
            meet.setStatus(DELETED.name());
            return meet;
        }
        return doMap(source);
    }

    Meet doMap(MeetJpa source);

    @IterableMapping(qualifiedByName = "map")
    List<Meet> map(List<MeetJpa> source);

    default String mapStatus(MeetStatus status) {
        return status.name();
    }
}
