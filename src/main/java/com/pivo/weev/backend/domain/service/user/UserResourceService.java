package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.ANY;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.buildUserSearchSpecification;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.UserRoles.USER;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.UserMapper;
import com.pivo.weev.backend.domain.mapping.jpa.UserJpaMapper;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.RegisteredUserSnapshot;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserRoleJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRolesRepository;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepository;
import com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserResourceService {

    private final UsersRepository usersRepository;
    private final UserRolesRepository userRolesRepository;

    private final UserPasswordService userPasswordService;

    public Optional<UserJpa> findUserJpa(Contacts contacts) {
        Specification<UserJpa> specification = buildUserSearchSpecification(null, contacts.getEmail(), contacts.getPhoneNumber());
        return usersRepository.find(specification);
    }

    public Optional<UserJpa> findUserJpa(RegisteredUserSnapshot registeredUserSnapshot) {
        Contacts contacts = registeredUserSnapshot.getContacts();
        Specification<UserJpa> specification = buildUserSearchSpecification(registeredUserSnapshot.getNickname(), contacts.getEmail(), contacts.getPhoneNumber());
        return usersRepository.find(specification);
    }

    public Optional<UserJpa> findUserJpa(String username) {
        return findUserJpa(username, ANY);
    }

    public Optional<UserJpa> findUserJpa(String username, UsernameType usernameType) {
        Specification<UserJpa> specification = buildUserSearchSpecification(username, usernameType);
        return usersRepository.find(specification);
    }

    public UserJpa fetchUserJpa(Long id) {
        return usersRepository.fetch(id);
    }

    @Transactional
    public User fetchUser(Long id) {
        UserJpa user = usersRepository.fetch(id);
        return getMapper(UserMapper.class).map(user);
    }

    public void createUser(RegisteredUserSnapshot registeredUserSnapshot) {
        UserJpa user = getMapper(UserJpaMapper.class).map(registeredUserSnapshot);
        fillPersistenceData(user);
        userPasswordService.updatePassword(user, registeredUserSnapshot.getPassword(), false);
        usersRepository.save(user);
    }

    private void fillPersistenceData(UserJpa user) {
        UserRoleJpa role = userRolesRepository.fetchByName(USER);
        user.setRole(role);
    }

    public boolean isNicknameAvailable(String nickname) {
        Specification<UserJpa> specification = buildUserSearchSpecification(nickname, NICKNAME);
        return !usersRepository.exists(specification);
    }
}
