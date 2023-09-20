package com.pivo.weev.backend.rest.model.common;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PageRest<T> {

    private List<T> content;
    private Integer number;

    public List<T> getContent() {
        if (isNull(content)) {
            content = new ArrayList<>();
        }
        return content;
    }
}
