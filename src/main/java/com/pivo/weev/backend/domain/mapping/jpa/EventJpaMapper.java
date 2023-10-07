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
    void map(CreatableEvent source, @MappingTarget EventJpa target);

    default void remap(EventJpa source, @MappingTarget EventJpa destination) {
        destination.setLocation(source.getLocation());
        destination.setCategory(source.getCategory());
        destination.setSubcategory(source.getSubcategory());
        destination.setDescription(source.getDescription());
        destination.setHeader(source.getHeader());
        destination.setEndTimeZoneId(source.getEndTimeZoneId());
        destination.setStartTimeZoneId(source.getStartTimeZoneId());
        destination.setLocalStartDateTime(source.getLocalStartDateTime());
        destination.setUtcStartDateTime(source.getUtcStartDateTime());
        destination.setLocalEndDateTime(source.getLocalEndDateTime());
        destination.setUtcEndDateTime(source.getUtcEndDateTime());
        destination.setEntryFee(source.getEntryFee());
        destination.setMembersLimit(source.getMembersLimit());
        destination.setRestrictions(source.getRestrictions());
        destination.setPhoto(source.getPhoto());
        destination.setStatus(source.getStatus());
    }
}
