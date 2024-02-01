package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.model.user.Device;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = {DeviceSettingsJpaMapper.class})
public interface DeviceJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "settings", source = "settings")
    void map(Device source, @MappingTarget DeviceJpa destination);
}
