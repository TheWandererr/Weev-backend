package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.ANY;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.buildUserSearchSpecification;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.UserRoles.USER;
import static com.pivo.weev.backend.domain.persistance.utils.PageableUtils.build;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.USED_NICKNAME_ERROR;
import static java.util.Optional.ofNullable;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.pivo.weev.backend.domain.mapping.domain.ImageMapper;
import com.pivo.weev.backend.domain.mapping.domain.MeetRequestMapper;
import com.pivo.weev.backend.domain.mapping.domain.UserMapper;
import com.pivo.weev.backend.domain.mapping.jpa.CloudResourceJpaMapper;
import com.pivo.weev.backend.domain.mapping.jpa.DeviceJpaMapper;
import com.pivo.weev.backend.domain.mapping.jpa.UserJpaMapper;
import com.pivo.weev.backend.domain.model.common.CloudResource;
import com.pivo.weev.backend.domain.model.common.Image;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.file.UploadableImage;
import com.pivo.weev.backend.domain.model.meet.MeetJoinRequest;
import com.pivo.weev.backend.domain.model.meet.SearchParams.PageCriteria;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.Device;
import com.pivo.weev.backend.domain.model.user.Device.Settings;
import com.pivo.weev.backend.domain.model.user.RegisteredUserSnapshot;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJoinRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserRoleJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.CloudResourceRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.DeviceRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetJoinRequestsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRolesRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType;
import com.pivo.weev.backend.domain.service.image.ImageCloudService;
import com.pivo.weev.backend.domain.service.image.ImageCompressingService;
import com.pivo.weev.backend.domain.service.message.DocumentService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepositoryWrapper usersRepository;
    private final UserRolesRepositoryWrapper userRolesRepository;
    private final MeetJoinRequestsRepositoryWrapper meetJoinRequestsRepository;
    private final DeviceRepositoryWrapper deviceRepository;
    private final CloudResourceRepositoryWrapper cloudResourceRepository;

    private final PasswordService passwordService;
    private final DocumentService documentService;
    private final ImageCompressingService imageCompressingService;
    private final ImageCloudService imageCloudService;

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

    public void createUser(RegisteredUserSnapshot registeredUserSnapshot) {
        UserJpa user = getMapper(UserJpaMapper.class).map(registeredUserSnapshot);
        fillPersistenceData(user);
        updatePassword(user, registeredUserSnapshot.getPassword());
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

    public void updatePassword(UserJpa user, String newPassword) {
        String encodedPassword = passwordService.encodePassword(newPassword);
        user.setPassword(encodedPassword);
        if (user.hasEmail()) {
            documentService.sendChangePasswordMail(user.getEmail(), user.getNickname());
        }
    }

    public void updatePassword(UserJpa user, String oldPassword, String newPassword) {
        passwordService.checkPasswordsMatching(user, oldPassword, newPassword);
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

    @Transactional
    public User findUser(Long id) {
        UserJpa user = usersRepository.fetch(id);
        return getMapper(UserMapper.class).map(user);
    }

    @Transactional
    public User updateUser(User sample) {
        if (isNicknameAvailable(sample.getNickname())) {
            throw new FlowInterruptedException(USED_NICKNAME_ERROR, null, BAD_REQUEST);
        }
        UserJpa user = usersRepository.fetch(sample.getId());
        getMapper(UserJpaMapper.class).update(sample, user);
        return getMapper(UserMapper.class).map(user);
    }

    @Transactional
    public Image updatePhoto(Long id, MultipartFile photo) {
        UserJpa user = usersRepository.fetch(id);
        ofNullable(photo).ifPresentOrElse(file -> updatePhoto(user, file), () -> deletePhoto(user));
        return getMapper(ImageMapper.class).map(user.getAvatar());
    }

    private void updatePhoto(UserJpa user, MultipartFile photo) {
        UploadableImage compressedPhoto = imageCompressingService.compress(photo);
        CloudResource cloudResource = imageCloudService.upload(compressedPhoto);
        CloudResourceJpa avatar = getMapper(CloudResourceJpaMapper.class).map(cloudResource);
        user.setAvatar(avatar);
    }

    private void deletePhoto(UserJpa user) {
        CloudResourceJpa photo = user.getAvatar();
        cloudResourceRepository.forceDelete(photo);
        imageCloudService.delete(photo.getBlobId());
    }
}
