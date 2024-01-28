package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.ANY;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.buildUserSearchSpecification;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.UserRoles.USER;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.UserJpaMapper;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserRoleJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRolesRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepositoryWrapper usersRepository;
    private final UserRolesRepositoryWrapper userRolesRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserJpa> findUser(Contacts contacts) {
        Specification<UserJpa> specification = buildUserSearchSpecification(null, contacts.getEmail(), contacts.getPhoneNumber());
        return usersRepository.find(specification);
    }

    public Optional<UserJpa> findUser(UserSnapshot userSnapshot) {
        Contacts contacts = userSnapshot.getContacts();
        Specification<UserJpa> specification = buildUserSearchSpecification(userSnapshot.getNickname(), contacts.getEmail(), contacts.getPhoneNumber());
        return usersRepository.find(specification);
    }

    public Optional<UserJpa> findUser(String username) {
        Specification<UserJpa> specification = buildUserSearchSpecification(username, ANY);
        return usersRepository.find(specification);
    }

    public UserJpa createUser(UserSnapshot userSnapshot) {
        UserJpa user = getMapper(UserJpaMapper.class).map(userSnapshot);
        fillPersistenceData(user);
        updatePassword(user, userSnapshot.getPassword());
        return usersRepository.save(user);
    }

    private void fillPersistenceData(UserJpa user) {
        UserRoleJpa role = userRolesRepository.fetchByName(USER);
        user.setRole(role);
    }

    public boolean isNicknameAvailable(String nickname) {
        Specification<UserJpa> specification = buildUserSearchSpecification(nickname, NICKNAME);
        return usersRepository.find(specification).isEmpty();
    }

    public void updatePassword(UserJpa user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
