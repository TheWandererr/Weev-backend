package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IUserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryWrapper extends GenericRepositoryWrapper<Long, UserJpa, IUserRepository> {

    public UserRepositoryWrapper(IUserRepository repository) {
        super(repository, ResourceName.USER);
    }
}
