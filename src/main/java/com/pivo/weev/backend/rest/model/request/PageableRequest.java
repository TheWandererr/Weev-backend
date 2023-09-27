package com.pivo.weev.backend.rest.model.request;

import static java.util.Objects.nonNull;

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
    private Integer pageSize;

    public boolean hasPageSize() {
        return nonNull(pageSize);
    }

}
