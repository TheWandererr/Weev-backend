package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.USER_ROLE;

import com.pivo.weev.backend.domain.persistance.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserRoleJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IUserRolesRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRolesRepositoryWrapper extends GenericRepositoryWrapper<Long, UserRoleJpa, IUserRolesRepository> {

    protected UserRolesRepositoryWrapper(IUserRolesRepository repository) {
        super(repository, USER_ROLE);
    }

    public UserRoleJpa fetchByName(String name) {
        return repository.findByName(name)
                         .orElseThrow(() -> new ResourceNotFoundException(notFound()));
    }
}
