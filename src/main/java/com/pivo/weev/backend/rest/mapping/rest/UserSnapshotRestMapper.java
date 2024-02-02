package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.rest.model.user.UserSnapshotRest;
import org.mapstruct.Mapper;

@Mapper(uses = {ImageRestMapper.class})
public interface UserSnapshotRestMapper {

    UserSnapshotRest map(User source);
}
