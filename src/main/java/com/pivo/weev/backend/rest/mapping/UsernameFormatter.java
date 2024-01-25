package com.pivo.weev.backend.rest.mapping;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface UsernameFormatter {

    @Named("formatUsername")
    default String formatUsername(String input) {
        return isBlank(input) ? null : input.toLowerCase();
    }
}
