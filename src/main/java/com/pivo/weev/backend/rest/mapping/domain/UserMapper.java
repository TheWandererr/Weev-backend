package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.rest.model.request.ProfileUpdateRequest;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    User map(ProfileUpdateRequest source);
}
