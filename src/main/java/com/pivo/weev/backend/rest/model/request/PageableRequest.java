package com.pivo.weev.backend.rest.model.request;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.INVALID_PAGE_SIZE;
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
    @Max(value = 1000, message = INVALID_PAGE_SIZE)
    @Min(value = 5, message = INVALID_PAGE_SIZE)
    private Integer pageSize;

    public boolean hasPageSize() {
        return nonNull(pageSize);
    }

}
