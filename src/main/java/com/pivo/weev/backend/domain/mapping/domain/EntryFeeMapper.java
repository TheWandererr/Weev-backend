package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.meet.EntryFee;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.EntryFeeJpa;
import org.mapstruct.Mapper;

@Mapper
public interface EntryFeeMapper {

    EntryFee map(EntryFeeJpa source);
}
