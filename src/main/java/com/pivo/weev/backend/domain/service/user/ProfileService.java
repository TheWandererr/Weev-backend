package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.USED_NICKNAME_ERROR;
import static java.util.Optional.ofNullable;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.pivo.weev.backend.domain.mapping.domain.ImageMapper;
import com.pivo.weev.backend.domain.mapping.domain.MeetsStatisticsMapper;
import com.pivo.weev.backend.domain.mapping.domain.UserMapper;
import com.pivo.weev.backend.domain.mapping.jpa.CloudResourceJpaMapper;
import com.pivo.weev.backend.domain.mapping.jpa.UserJpaMapper;
import com.pivo.weev.backend.domain.model.common.CloudResource;
import com.pivo.weev.backend.domain.model.common.Image;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.file.UploadableImage;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.projection.MeetsStatisticsProjection;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.CloudResourceRepository;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepository;
import com.pivo.weev.backend.domain.service.image.ImageCloudService;
import com.pivo.weev.backend.domain.service.image.ImageCompressingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserResourceService userResourceService;
    private final ImageCompressingService imageCompressingService;
    private final ImageCloudService imageCloudService;

    private final CloudResourceRepository cloudResourceRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public User updateProfile(User sample) {
        if (!userResourceService.isNicknameAvailable(sample.getNickname())) {
            throw new FlowInterruptedException(USED_NICKNAME_ERROR, null, BAD_REQUEST);
        }
        UserJpa user = userResourceService.fetchJpa(sample.getId());
        getMapper(UserJpaMapper.class).update(sample, user);

        User profile = getMapper(UserMapper.class).map(user);
        setMeetStatistics(profile);
        return profile;
    }

    private void setMeetStatistics(User user) {
        MeetsStatisticsProjection statistics = usersRepository.getMeetsStatistics(user.getId());
        user.setMeetsStatistics(getMapper(MeetsStatisticsMapper.class).map(statistics));
    }

    @Transactional
    public Image updatePhoto(Long id, MultipartFile photo) {
        UserJpa user = userResourceService.fetchJpa(id);
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

    public User getProfile(Long id) {
        User user = userResourceService.fetchUser(id);
        setMeetStatistics(user);
        return user;
    }
}
