package com.pivo.weev.backend.integration.service.cloudinary;

import static com.pivo.weev.backend.common.utils.IOUtils.getBytes;

import com.pivo.weev.backend.integration.client.cloudinary.CloudinaryClient;
import com.pivo.weev.backend.integration.client.cloudinary.model.Image;
import java.awt.image.BufferedImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final CloudinaryClient client;

    public Image upload(com.pivo.weev.backend.domain.model.file.Image image) {
        return client.upload(getBytes(image.getSource(), image.getFormat()));
    }
}
