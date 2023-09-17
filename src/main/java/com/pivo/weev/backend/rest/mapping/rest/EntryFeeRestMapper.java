package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.event.EntryFee;
import com.pivo.weev.backend.rest.model.event.EntryFeeRest;
import org.mapstruct.Mapper;

@Mapper
public interface EntryFeeRestMapper {

    EntryFeeRest map(EntryFee source);
}
