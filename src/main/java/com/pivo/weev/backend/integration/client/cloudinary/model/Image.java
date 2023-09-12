package com.pivo.weev.backend.integration.client.cloudinary.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image {

  @JsonProperty("secure_url")
  private String url;
  @JsonProperty("public_id")
  private String publicId;
  @JsonProperty("asset_id")
  private String assetId;
  private Integer height;
  private Integer width;
  private String format;
  private String signature;
}
