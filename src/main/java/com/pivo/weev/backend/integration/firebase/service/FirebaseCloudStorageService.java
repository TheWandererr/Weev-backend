package com.pivo.weev.backend.integration.firebase.service;

import static com.pivo.weev.backend.utils.IOUtils.getBytes;

import com.google.cloud.storage.Blob;
import com.pivo.weev.backend.domain.model.file.UploadableImage;
import com.pivo.weev.backend.integration.firebase.client.CloudStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseCloudStorageService {

    private final CloudStorageClient client;

    public Blob upload(UploadableImage uploadableImage) {
        String format = uploadableImage.getFormat();
        byte[] content = getBytes(uploadableImage.getSource(), format);
        return client.upload(content, uploadableImage.getName(), format);
    }

    public void delete(String blobId) {
        client.delete(blobId);
    }
}
