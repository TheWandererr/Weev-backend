package com.pivo.weev.backend.integration.firebase;

import static com.pivo.weev.backend.utils.IOUtils.getBytes;

import com.google.cloud.storage.Blob;
import com.pivo.weev.backend.integration.firebase.client.CloudStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloudStorageService {

    private final CloudStorageClient client;

    public Blob upload(com.pivo.weev.backend.domain.model.file.Image image) {
        String format = image.getFormat();
        byte[] content = getBytes(image.getSource(), format);
        return client.upload(content, image.getName(), format);
    }

    public void delete(String blobId) {
        client.delete(blobId);
    }
}
