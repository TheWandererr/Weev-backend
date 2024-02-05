package com.pivo.weev.backend.domain.mapping.domain.decorator;

import static com.pivo.weev.backend.domain.model.user.User.deleted;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.mapping.domain.UserMapper;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class UserMapperDecorator implements UserMapper {

    private final UserMapper delegate;

    @Override
    public User map(UserJpa source) {
        if (isNull(source)) {
            return null;
        }
        if (source.isDeleted()) {
            return deleted();
        }
        return delegate.map(source);
    }
}
