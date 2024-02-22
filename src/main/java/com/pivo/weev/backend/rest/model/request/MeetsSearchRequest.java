package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.rest.utils.Constants.MapParams.MAXIMUM_ZOOM;
import static com.pivo.weev.backend.rest.utils.Constants.MapParams.MINIMAL_ZOOM;
import static com.pivo.weev.backend.rest.utils.Constants.PageableParams.MEETS_PER_PAGE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_AMOUNT_ERROR;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.rest.model.common.BoundingBoxRest;
import com.pivo.weev.backend.rest.model.meet.RadiusRest;
import com.pivo.weev.backend.rest.model.meet.RestrictionsRest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetsSearchRequest extends PageableRequest {

    private String header;
    private String category;
    private String subcategory;
    @Valid
    private RadiusRest radius;
    @Min(value = MINIMAL_ZOOM, message = INVALID_AMOUNT_ERROR)
    @Max(value = MAXIMUM_ZOOM, message = INVALID_AMOUNT_ERROR)
    private Integer zoom;
    private String geoHash;
    private RestrictionsRest restrictions;
    private BoundingBoxRest bbox;

    public MeetsSearchRequest(Integer page) {
        super(page, MEETS_PER_PAGE, new String[]{});
    }

    public boolean hasRestrictions() {
        return nonNull(restrictions);
    }
}
