package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IUsersRepository;
import org.springframework.stereotype.Component;

@Component
public class UsersRepositoryWrapper extends GenericRepositoryWrapper<Long, UserJpa, IUsersRepository> {

    public UsersRepositoryWrapper(IUsersRepository repository) {
        super(repository, ResourceName.USER);
    }
}
