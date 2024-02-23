package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_AMOUNT_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_LENGTH_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.LENGTH_OUT_OF_BOUND_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_NULL_ERROR;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.rest.model.meet.LocationRest;
import com.pivo.weev.backend.rest.model.meet.RestrictionsRest;
import com.pivo.weev.backend.rest.validation.annotation.NullableNotBlank;
import com.pivo.weev.backend.rest.validation.annotation.ValidImage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MeetSaveRequest {

    private Long id;
    @Size(min = 3, max = 120, message = INVALID_LENGTH_ERROR)
    @NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
    private String header;
    @NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
    private String category;
    @NotBlank(message = MUST_BE_NOT_BLANK_ERROR)
    private String subcategory;
    @NotNull(message = MUST_BE_NOT_NULL_ERROR)
    @Valid
    private LocationRest location;
    @Min(value = 1, message = INVALID_AMOUNT_ERROR)
    private Integer membersLimit;
    @Size(max = 255, message = LENGTH_OUT_OF_BOUND_ERROR)
    @NullableNotBlank
    private String description;
    @ValidImage
    private MultipartFile photo;
    private boolean updatePhoto;
    private RestrictionsRest restrictions = RestrictionsRest.withDefaults();
    @NotNull(message = MUST_BE_NOT_NULL_ERROR)
    private LocalDateTime localStartDateTime;
    @NotNull(message = MUST_BE_NOT_NULL_ERROR)
    private LocalDateTime localEndDateTime;
    private boolean saveAsTemplate;

    public RestrictionsRest getRestrictions() {
        if (isNull(restrictions)) {
            restrictions = new RestrictionsRest();
        }
        return restrictions;
    }
}
