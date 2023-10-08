package com.pivo.weev.backend.domain.persistance.jpa.utils;

import static com.pivo.weev.backend.common.utils.ArrayUtils.mapToList;
import static com.pivo.weev.backend.common.utils.ArrayUtils.toArray;
import static com.pivo.weev.backend.common.utils.Constants.Symbols.DOT;

import jakarta.persistence.metamodel.Attribute;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SpecificationUtils {

    private static String build(Attribute<?, ?>[] attributes) {
        List<String> names = mapToList(attributes, Attribute::getName);
        return String.join(DOT, names);
    }

    public static String fieldPathFrom(Attribute<?, ?>... attributes) {
        return build(toArray(attributes));
    }
}
