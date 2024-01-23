package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_AMOUNT;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_LENGTH;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.LENGTH_OUT_OF_BOUND;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_BLANK;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MUST_BE_NOT_NULL;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.rest.model.event.EntryFeeRest;
import com.pivo.weev.backend.rest.model.event.LocationRest;
import com.pivo.weev.backend.rest.model.event.RestrictionsRest;
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
public class EventSaveRequest {

    private Long id;
    @Size(min = 3, max = 120, message = INVALID_LENGTH)
    @NotBlank(message = MUST_BE_NOT_BLANK)
    private String header;
    @NotBlank(message = MUST_BE_NOT_BLANK)
    private String category;
    @NotBlank(message = MUST_BE_NOT_BLANK)
    private String subcategory;
    @NotNull(message = MUST_BE_NOT_NULL)
    @Valid
    private LocationRest location;
    @Min(value = 1, message = INVALID_AMOUNT)
    private Integer membersLimit;
    @Size(max = 255, message = LENGTH_OUT_OF_BOUND)
    @NullableNotBlank
    private String description;
    @ValidImage
    private MultipartFile photo;
    private boolean updatePhoto;
    private EntryFeeRest entryFee;
    private RestrictionsRest restrictions = RestrictionsRest.withDefaults();
    @NotNull(message = MUST_BE_NOT_NULL)
    private LocalDateTime localStartDateTime;
    @NotNull(message = MUST_BE_NOT_NULL)
    private LocalDateTime localEndDateTime;

    public RestrictionsRest getRestrictions() {
        if (isNull(restrictions)) {
            restrictions = new RestrictionsRest();
        }
        return restrictions;
    }
}
