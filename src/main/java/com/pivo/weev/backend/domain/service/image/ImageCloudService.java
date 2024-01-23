package com.pivo.weev.backend.domain.service.image;

import static org.mapstruct.factory.Mappers.getMapper;

import com.google.cloud.storage.Blob;
import com.pivo.weev.backend.domain.mapping.jpa.CloudResourceJpaMapper;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.integration.firebase.CloudStorageService;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageCloudService {

    private final CloudStorageService cloudService;

    public CloudResourceJpa upload(com.pivo.weev.backend.domain.model.file.Image source) {
        Blob blob = cloudService.upload(source);
        URL downloadUrl = blob.signUrl(36500, TimeUnit.DAYS);
        return getMapper(CloudResourceJpaMapper.class).map(blob, downloadUrl);
    }

    public void delete(String blobId) {
        cloudService.delete(blobId);
    }
}
