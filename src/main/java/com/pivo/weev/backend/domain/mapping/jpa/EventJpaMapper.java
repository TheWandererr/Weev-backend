package com.pivo.weev.backend.domain.mapping.jpa;

import static org.mapstruct.ReportingPolicy.IGNORE;

import com.pivo.weev.backend.common.utils.DateTimeUtils;
import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        imports = {DateTimeUtils.class, LocationJpaMapper.class, RestrictionsJpaMapper.class, EntryFeeJpaMapper.class}, uses = {RestrictionsJpaMapper.class},
        unmappedTargetPolicy = IGNORE
)
public interface EventJpaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "subcategory", ignore = true)
    @Mapping(target = "moderatedBy", ignore = true)
    @Mapping(target = "updatableTarget", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "utcStartDateTime", expression = "java(DateTimeUtils.toInstant(source.getLocalStartDateTime(), source.getStartTimeZoneId()))")
    @Mapping(target = "utcEndDateTime", expression = "java(DateTimeUtils.toInstant(source.getLocalEndDateTime(), source.getEndTimeZoneId()))")
    @Mapping(target = "reminded", constant = "false")
    @Mapping(target = "status", constant = "ON_MODERATION")
    EventJpa map(CreatableEvent source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "moderatedBy", ignore = true)
    @Mapping(target = "updatableTarget", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void map(EventJpa source, @MappingTarget EventJpa destination);
}
