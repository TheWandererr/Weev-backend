package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.EntryFee;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EntryFeeJpa;
import org.mapstruct.Mapper;

@Mapper
public interface EntryFeeMapper {

    EntryFee map(EntryFeeJpa source);
}
