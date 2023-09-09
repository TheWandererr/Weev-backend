package com.pivo.weev.backend.rest.model.request;

import com.pivo.weev.backend.rest.model.event.EntryFeeRest;
import com.pivo.weev.backend.rest.model.event.LocationRest;
import com.pivo.weev.backend.rest.model.event.RestrictionsRest;
import com.pivo.weev.backend.rest.validation.annotation.NullableNotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class EventSaveRequest {

  @Size(min = 3, max = 120, message = "length must be between 3 and 120")
  @NotBlank(message = "must be not blank")
  private String header;
  @NotBlank(message = "unsupported category")
  private String category;
  @NotBlank(message = "unsupported subcategory")
  private String subcategory;
  @NotNull(message = "must be not null")
  @Valid
  private LocationRest location;
  @Min(value = 1, message = "must be more than 0")
  private Integer membersLimit;
  @Size(max = 255, message = "length out of bound")
  @NullableNotBlank(message = "must be null or not blank")
  private String description;
  private MultipartFile photo;
  private EntryFeeRest entryFee;
  private RestrictionsRest restrictions;
}
