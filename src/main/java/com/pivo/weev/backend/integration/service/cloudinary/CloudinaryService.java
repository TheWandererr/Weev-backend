package com.pivo.weev.backend.integration.service.cloudinary;

import com.pivo.weev.backend.integration.client.cloudinary.CloudinaryClient;
import com.pivo.weev.backend.integration.client.cloudinary.model.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final CloudinaryClient client;

    public Image upload(MultipartFile file) {
        return client.upload(file);
    }
}
