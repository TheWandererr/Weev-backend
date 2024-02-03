package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.rest.model.user.ProfileRest;
import org.mapstruct.Mapper;

@Mapper(uses = {ImageRestMapper.class})
public interface ProfileRestMapper {

    ProfileRest map(User source);
}
