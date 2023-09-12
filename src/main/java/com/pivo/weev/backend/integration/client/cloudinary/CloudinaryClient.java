package com.pivo.weev.backend.integration.client.cloudinary;

import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.CLOUD_OPERATION_ERROR;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.integration.client.cloudinary.model.Image;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CloudinaryClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(CloudinaryClient.class);

  private final CloudinaryProperties cloudinaryProperties;
  private final Cloudinary api;
  private final ObjectMapper mapper;

  public CloudinaryClient(CloudinaryProperties cloudinaryProperties, @Qualifier("mapper") ObjectMapper mapper) {
    this.mapper = mapper;
    LOGGER.info("Client Initialization is started");
    this.cloudinaryProperties = cloudinaryProperties;
    final Map<String, Object> config = Map.of(
        "cloud_name", cloudinaryProperties.getCloudName(),
        "api_key", cloudinaryProperties.getApiKey(),
        "api_secret", cloudinaryProperties.getApiSecret(),
        "secure", true
    );
    this.api = new Cloudinary(config);
    LOGGER.info("Client Initialization is finished");
  }

  public Image upload(MultipartFile file) {
    try {
      Object response = api.uploader()
                           .upload(file.getBytes(), cloudinaryProperties.getUploadParams());
      return mapper.convertValue(response, Image.class);
    } catch (IOException exception) {
      String reason = ofNullable(exception.getCause()).map(Throwable::getMessage).orElse(null);
      throw new ReasonableException(CLOUD_OPERATION_ERROR, reason, NOT_ACCEPTABLE);
    }
  }
}
