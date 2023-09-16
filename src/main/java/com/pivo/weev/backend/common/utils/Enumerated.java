package com.pivo.weev.backend.common.utils;

import static com.pivo.weev.backend.common.utils.ArrayUtils.isEmpty;
import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Optional;

public interface Enumerated {

    String getName();

    static <T extends Enumerated> String[] names(Class<T> clazz) {
        final T[] enumConstants = clazz.getEnumConstants();
        if (isEmpty(enumConstants)) {
            return new String[0];
        }
        return Arrays.toString(enumConstants).replaceAll("^.|.$", "").split(", ");
    }

    static <T extends Enumerated> Optional<T> findByName(String name, Class<T> clazz) {
        return CollectionUtils.findFirst(asList(clazz.getEnumConstants()), enumerated -> enumerated.getName().equalsIgnoreCase(name));
    }

    static <T extends Enumerated> T getByName(String name, Class<T> clazz) {
        return findByName(name, clazz).orElseGet(() -> null);
    }
}
