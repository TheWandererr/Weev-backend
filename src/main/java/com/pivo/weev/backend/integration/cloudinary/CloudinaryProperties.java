package com.pivo.weev.backend.integration.cloudinary;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "integration.client.cloudinary")
@Getter
@Setter
public class CloudinaryProperties {

    private static final Map<Object, Object> UPLOAD_PARAMS = Map.of(
            "use_filename", false,
            "unique_filename", true,
            "resource_type", "image"
    );

    private String apiKey;
    private String apiSecret;
    private String cloudName;

    public Map<Object, Object> getUploadParams() {
        return UPLOAD_PARAMS;
    }

}
