package com.pivo.weev.backend.domain.service.image;

import static org.mapstruct.factory.Mappers.getMapper;

import com.google.cloud.storage.Blob;
import com.pivo.weev.backend.domain.model.common.CloudResource;
import com.pivo.weev.backend.domain.model.file.UploadableImage;
import com.pivo.weev.backend.integration.firebase.service.CloudStorageService;
import com.pivo.weev.backend.integration.mapping.domain.CloudResourceMapper;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageCloudService {

    private final CloudStorageService cloudService;

    public CloudResource upload(UploadableImage source) {
        Blob blob = cloudService.upload(source);
        URL downloadUrl = blob.signUrl(36500, TimeUnit.DAYS);
        return getMapper(CloudResourceMapper.class).map(blob, downloadUrl);
    }

    public void delete(String blobId) {
        cloudService.delete(blobId);
    }
}
