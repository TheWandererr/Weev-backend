package com.pivo.weev.backend.domain.service.files;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.CloudResourceJpaMapper;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.integration.client.cloudinary.model.Image;
import com.pivo.weev.backend.integration.service.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilesUploadingService {

    private final CloudinaryService cloudinaryService;

    public CloudResourceJpa upload(com.pivo.weev.backend.domain.model.file.Image source) {
        Image image = cloudinaryService.upload(source);
        return getMapper(CloudResourceJpaMapper.class).map(image);
    }

}
