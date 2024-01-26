package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.UserRoles.USER;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.UserJpaMapper;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserRoleJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRolesRepositoryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFactory {

    private final UserRolesRepositoryWrapper userRolesRepository;

    public UserJpa createUser(UserSnapshot userSnapshot) {
        UserJpa user = getMapper(UserJpaMapper.class).map(userSnapshot);
        fillPersistenceData(user);
        return user;
    }

    private void fillPersistenceData(UserJpa user) {
        UserRoleJpa role = userRolesRepository.fetchByName(USER);
        user.setRole(role);
    }
}
