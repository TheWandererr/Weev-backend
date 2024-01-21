package com.pivo.weev.backend.domain.service.event;

import com.pivo.weev.backend.domain.model.event.CreatableEvent;
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
            createPhoto(destination, source.getPhoto());
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
        deletePhoto(eventJpa);
        createPhoto(eventJpa, photo);
    }

    private void createPhoto(EventJpa eventJpa, MultipartFile photo) {
        Image compressedPhoto = imageCompressingService.compress(photo);
        CloudResourceJpa cloudResourceJpa = imageCloudService.upload(compressedPhoto);
        eventJpa.setPhoto(cloudResourceJpa);
    }
}
