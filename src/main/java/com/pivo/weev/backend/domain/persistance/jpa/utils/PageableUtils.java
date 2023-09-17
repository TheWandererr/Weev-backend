package com.pivo.weev.backend.domain.persistance.jpa.utils;

import static com.pivo.weev.backend.domain.utils.Constants.PageableParams.EVENTS_PER_PAGE;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageableUtils {

    public static Pageable build(Integer page, Integer elementsPerPage, String[] sortFields) {
        Sort sort = Sort.by(sortFields);
        return PageRequest.of(page, elementsPerPage, sort);
    }
}
