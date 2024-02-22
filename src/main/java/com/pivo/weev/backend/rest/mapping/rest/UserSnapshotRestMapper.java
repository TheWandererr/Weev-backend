package com.pivo.weev.backend.rest.mapping.rest;

import static com.pivo.weev.backend.domain.utils.JwtUtils.getUserId;

import com.pivo.weev.backend.domain.model.messaging.payload.UserPayload;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.rest.model.user.UserSnapshotRest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.oauth2.jwt.Jwt;

@Mapper(uses = {ImageRestMapper.class})
public interface UserSnapshotRestMapper {

    UserSnapshotRest map(User source);

    default UserSnapshotRest map(Jwt accessToken) {
        UserSnapshotRest destination = new UserSnapshotRest();
        destination.setId(getUserId(accessToken));
        return destination;
    }

    @Mapping(target = "avatar", source = "avatarUrl")
    UserSnapshotRest map(UserPayload source);
}
