package com.pivo.weev.backend.domain.service.image;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.CloudResourceJpaMapper;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.integration.cloudinary.model.Image;
import com.pivo.weev.backend.integration.cloudinary.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageCloudService {

    private final CloudinaryService cloudinaryService;

    public CloudResourceJpa upload(com.pivo.weev.backend.domain.model.file.Image source) {
        Image image = cloudinaryService.upload(source);
        return getMapper(CloudResourceJpaMapper.class).map(image);
    }

    public void delete(String externalId) {
        cloudinaryService.destroy(externalId);
    }

}
