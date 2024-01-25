package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.CLOUD_OPERATION_ERROR;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.pivo.weev.backend.domain.mapping.jpa.CloudResourceJpaMapper;
import com.pivo.weev.backend.domain.model.common.CloudResource;
import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.file.Image;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.CloudResourceRepositoryWrapper;
import com.pivo.weev.backend.domain.service.image.ImageCloudService;
import com.pivo.weev.backend.domain.service.image.ImageCompressingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventImageService {

    private final CloudResourceRepositoryWrapper cloudResourceRepository;

    private final ImageCloudService imageCloudService;
    private final ImageCompressingService imageCompressingService;

    public void updatePhoto(CreatableEvent source, EventJpa destination) {
        if (!source.isUpdatePhoto()) {
            return;
        }
        if (!source.hasPhoto()) {
            deletePhoto(destination);
        } else if (destination.hasPhoto()) {
            replacePhoto(destination, source.getPhoto());
        } else {
            CloudResourceJpa cloudResourceJpa = createPhoto(source.getPhoto());
            destination.setPhoto(cloudResourceJpa);
        }
    }

    public void deletePhoto(EventJpa eventJpa) {
        if (!eventJpa.hasPhoto()) {
            return;
        }
        CloudResourceJpa photo = eventJpa.getPhoto();
        cloudResourceRepository.forceDelete(photo);
        imageCloudService.delete(photo.getBlobId());
    }

    private void replacePhoto(EventJpa eventJpa, MultipartFile photo) {
        try {
            deletePhoto(eventJpa);
        } catch (Exception exception) {
            throw new FlowInterruptedException(CLOUD_OPERATION_ERROR, exception.getMessage(), INTERNAL_SERVER_ERROR);
        }
        CloudResourceJpa cloudResourceJpa = createPhoto(photo);
        eventJpa.setPhoto(cloudResourceJpa);
    }

    private CloudResourceJpa createPhoto(MultipartFile photo) {
        Image compressedPhoto = imageCompressingService.compress(photo);
        CloudResource cloudResource = imageCloudService.upload(compressedPhoto);
        return getMapper(CloudResourceJpaMapper.class).map(cloudResource);
    }
}
