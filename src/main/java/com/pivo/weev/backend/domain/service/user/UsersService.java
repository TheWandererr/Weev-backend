package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.ANY;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.buildUserSearchSpecification;

import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRepositoryWrapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepositoryWrapper userRepository;
    private final UserFactory userFactory;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserJpa> findUser(Contacts contacts) {
        Specification<UserJpa> specification = buildUserSearchSpecification(null, contacts.getEmail(), contacts.getPhoneNumber());
        return userRepository.find(specification);
    }

    public Optional<UserJpa> findUser(UserSnapshot userSnapshot) {
        Contacts contacts = userSnapshot.getContacts();
        Specification<UserJpa> specification = buildUserSearchSpecification(userSnapshot.getNickname(), contacts.getEmail(), contacts.getPhoneNumber());
        return userRepository.find(specification);
    }

    public Optional<UserJpa> findUser(String username) {
        Specification<UserJpa> specification = buildUserSearchSpecification(username, ANY);
        return userRepository.find(specification);
    }

    public UserJpa createNewUser(UserSnapshot userSnapshot) {
        UserJpa user = userFactory.createUser(userSnapshot);
        updatePassword(user, userSnapshot.getPassword());
        userRepository.save(user);
        return user;
    }

    public boolean isNicknameAvailable(String nickname) {
        Specification<UserJpa> specification = buildUserSearchSpecification(nickname, NICKNAME);
        return userRepository.find(specification).isEmpty();
    }

    public void updatePassword(UserJpa user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
