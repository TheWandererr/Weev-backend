package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.INVALID_AMOUNT;
import static java.util.Objects.nonNull;

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
public class PageableRequest {

    private Integer page = 0;
    @Max(value = 1000, message = INVALID_AMOUNT)
    @Min(value = 5, message = INVALID_AMOUNT)
    private Integer pageSize;

    public boolean hasPageSize() {
        return nonNull(pageSize);
    }

}
