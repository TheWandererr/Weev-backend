package com.pivo.weev.backend.integration.firebase.client;

import static com.google.firebase.cloud.StorageClient.getInstance;
import static com.pivo.weev.backend.utils.Constants.Symbols.DOT;
import static com.pivo.weev.backend.utils.Randomizer.uuid;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.pivo.weev.backend.integration.firebase.application.FirebaseApplication;
import org.springframework.stereotype.Component;

@Component
public class CloudStorageClient {

    private final Bucket api;

    public CloudStorageClient(FirebaseApplication application) {
        StorageClient instance = getInstance(application.getInstance());
        this.api = instance.bucket();
    }

    public Blob upload(byte[] content, String name, String contentType) {
        BlobId blobId = BlobId.of(api.getName(), uuid() + DOT + name);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                                    .setContentType(contentType)
                                    .build();
        return api.getStorage().create(blobInfo, content);
    }

    public void delete(String blobId) {
        api.getStorage()
           .delete(BlobId.of(api.getName(), blobId));
    }
}
