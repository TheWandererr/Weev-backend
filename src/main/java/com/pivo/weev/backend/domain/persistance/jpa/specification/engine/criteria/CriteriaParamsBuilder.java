package com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria;

import static com.pivo.weev.backend.common.utils.CollectionUtils.isEmpty;
import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.Paths.PATH_SPLITTER;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import com.pivo.weev.backend.common.utils.ArrayUtils;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaCompareParams;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaGet;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaJoin;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaParams;
import jakarta.persistence.metamodel.Attribute;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class CriteriaParamsBuilder {

    private CriteriaParamsBuilder() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class that cannot be initialized");
    }

    public static <E> CriteriaParams<E> buildCriteriaParams(Attribute<?, ?> attribute, int joins) {
        return buildCriteriaParams(attribute.getName(), joins);
    }

    public static <E> CriteriaParams<E> buildCriteriaParams(String fieldPath, int joins) {
        List<String> fieldsChain = ofNullable(fieldPath).map(path -> path.split(PATH_SPLITTER))
                                                        .map(ArrayUtils::toList)
                                                        .orElseGet(Collections::emptyList);
        if (isEmpty(fieldsChain)) {
            return new CriteriaParams<>();
        }
        List<CriteriaJoin> criteriaJoins = IntStream.range(0, joins)
                                                    .mapToObj(index -> new CriteriaJoin(fieldsChain.get(index)))
                                                    .collect(toList());
        List<CriteriaGet> criteriaGets = IntStream.range(joins, fieldsChain.size())
                                                  .mapToObj(index -> new CriteriaGet(fieldsChain.get(index)))
                                                  .collect(toList());
        return new CriteriaParams<>(criteriaJoins, criteriaGets);
    }

    public static <E> CriteriaParams<E> buildCriteriaParams(Attribute<?, ?> attribute, int joins, E value, Class<E> clazz) {
        return buildCriteriaParams(attribute.getName(), joins, value, clazz);
    }

    public static <E> CriteriaParams<E> buildCriteriaParams(String fieldPath, int joins, E value, Class<E> clazz) {
        CriteriaParams<E> criteriaParams = buildCriteriaParams(fieldPath, joins, clazz);
        criteriaParams.setCriteriaValue(value);
        return criteriaParams;
    }

    public static <E> CriteriaParams<E> buildCriteriaParams(Attribute<?, ?> attribute, int joins, Class<E> clazz) {
        return buildCriteriaParams(attribute.getName(), joins, clazz);
    }

    public static <E> CriteriaParams<E> buildCriteriaParams(String fieldPath, int joins, Class<E> clazz) {
        CriteriaParams<E> criteriaParams = buildCriteriaParams(fieldPath, joins);
        criteriaParams.setCriteriaClass(clazz);
        return criteriaParams;
    }

    @SuppressWarnings("unchecked")
    public static CriteriaCompareParams<List<Object>, Integer> buildCriteriaCompareListParams(String collectionFieldPath,
                                                                                              int collectionFieldsJoins,
                                                                                              String fieldPath,
                                                                                              int fieldJoins) {
        CriteriaParams<List<Object>> collectionParams = buildCriteriaParams(collectionFieldPath, collectionFieldsJoins);
        collectionParams.setCriteriaClass((Class<List<Object>>) (Object) List.class);
        CriteriaParams<Integer> comparableValueParams = buildCriteriaParams(fieldPath, fieldJoins);
        comparableValueParams.setCriteriaClass(Integer.class);
        return new CriteriaCompareParams<>(collectionParams, comparableValueParams);
    }

    @SuppressWarnings("unchecked")
    public static CriteriaCompareParams<Set<Object>, Integer> buildCriteriaCompareSetParams(String collectionFieldPath,
                                                                                            int collectionFieldsJoins,
                                                                                            String fieldPath,
                                                                                            int fieldJoins) {
        CriteriaParams<Set<Object>> collectionParams = buildCriteriaParams(collectionFieldPath, collectionFieldsJoins);
        collectionParams.setCriteriaClass((Class<Set<Object>>) (Object) Set.class);
        CriteriaParams<Integer> comparableValueParams = buildCriteriaParams(fieldPath, fieldJoins);
        comparableValueParams.setCriteriaClass(Integer.class);
        return new CriteriaCompareParams<>(collectionParams, comparableValueParams);
    }

}
