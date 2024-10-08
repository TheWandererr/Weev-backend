package com.pivo.weev.backend.domain.persistance.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@UtilityClass
public class PageableUtils {

    public static Pageable build(Integer page, Integer elementsPerPage, String[] sortFields, Direction direction) {
        Sort sort = Sort.by(direction, sortFields);
        return PageRequest.of(page, elementsPerPage, sort);
    }

    public static Pageable build(Integer page, Integer elementsPerPage, String[] sortFields) {
        Sort sort = Sort.by(sortFields);
        return PageRequest.of(page, elementsPerPage, sort);
    }
}
