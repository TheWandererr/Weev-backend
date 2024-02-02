package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.ANY;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.buildUserSearchSpecification;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.UserRoles.USER;
import static com.pivo.weev.backend.domain.persistance.utils.PageableUtils.build;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MeetRequestMapper;
import com.pivo.weev.backend.domain.mapping.jpa.DeviceJpaMapper;
import com.pivo.weev.backend.domain.mapping.jpa.UserJpaMapper;
import com.pivo.weev.backend.domain.model.meet.MeetJoinRequest;
import com.pivo.weev.backend.domain.model.meet.SearchParams.PageCriteria;
import com.pivo.weev.backend.domain.model.messaging.source.ChangePasswordSource;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.Device;
import com.pivo.weev.backend.domain.model.user.Device.Settings;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJoinRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserRoleJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.DeviceRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetJoinRequestsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRolesRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import com.pivo.weev.backend.domain.service.message.MailMessagingService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepositoryWrapper usersRepository;
    private final UserRolesRepositoryWrapper userRolesRepository;
    private final MeetJoinRequestsRepositoryWrapper meetJoinRequestsRepository;
    private final DeviceRepositoryWrapper deviceRepository;

    private final PasswordService passwordService;
    private final MailMessagingService mailMessagingService;

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

    public void createUser(UserSnapshot userSnapshot) {
        UserJpa user = getMapper(UserJpaMapper.class).map(userSnapshot);
        fillPersistenceData(user);
        updatePassword(user, userSnapshot.getPassword());
        usersRepository.save(user);
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
        String encodedPassword = passwordService.encodePassword(newPassword);
        user.setPassword(encodedPassword);
        if (user.hasEmail()) {
            mailMessagingService.sendChangePasswordMessage(user.getEmail(), new ChangePasswordSource(user.getNickname()));
        }
    }

    public void updatePassword(UserJpa user, String oldPassword, String newPassword) {
        passwordService.validatePasswords(user, oldPassword, newPassword);
        updatePassword(user, newPassword);
    }

    @Transactional
    public Settings updateDeviceSettings(Device device) {
        DeviceJpa deviceJpa = deviceRepository.fetchByUserIdAndInternalId(device.getUserId(), device.getId());
        getMapper(DeviceJpaMapper.class).map(device, deviceJpa);
        return device.getSettings();
    }

    @Transactional
    public Page<MeetJoinRequest> getMeetJoinRequests(Long meetId, PageCriteria pageCriteria) {
        Pageable pageable = build(pageCriteria.getPage(), pageCriteria.getPageSize(), new String[0]);
        Page<MeetJoinRequestJpa> jpaPage = meetJoinRequestsRepository.findAllByMeetId(meetId, pageable);
        List<MeetJoinRequest> content = getMapper(MeetRequestMapper.class).map(jpaPage.getContent());
        return new PageImpl<>(content, jpaPage.getPageable(), jpaPage.getTotalElements());
    }
}
