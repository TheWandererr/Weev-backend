package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserRoleJpa;
import java.util.Optional;

public interface IUserRolesRepository extends IGenericRepository<Long, UserRoleJpa> {

    Optional<UserRoleJpa> findByName(String name);
}
