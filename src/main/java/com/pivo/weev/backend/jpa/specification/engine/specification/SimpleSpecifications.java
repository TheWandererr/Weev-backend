package com.pivo.weev.backend.jpa.specification.engine.specification;


import static com.pivo.weev.backend.jpa.specification.engine.criteria.ExpressionBuilder.getExpression;
import static com.pivo.weev.backend.jpa.specification.engine.criteria.ExpressionBuilder.getPath;

import com.pivo.weev.backend.jpa.specification.engine.criteria.CriteriaParamsBuilder;
import com.pivo.weev.backend.jpa.specification.engine.criteria.model.CriteriaCompareParams;
import com.pivo.weev.backend.jpa.specification.engine.criteria.model.CriteriaParams;
import jakarta.persistence.criteria.Expression;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class SimpleSpecifications {

    private SimpleSpecifications() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class that cannot be initialized");
    }

    public static <T> Specification<T> empty() {
        return Specification.where(null);
    }

    public static <T> Specification<T> lessThan(String fieldPath, Integer value) {
        return lessThan(fieldPath, value, 0);
    }

    public static <T> Specification<T> lessThan(String fieldPath, Integer value, int joins) {
        return Objects.isNull(value)
                ? empty()
                : buildLessThanIntegerSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, Integer.class));
    }

    public static <T> Specification<T> lessThan(String fieldPath, Double value) {
        return lessThan(fieldPath, value, 0);
    }

    public static <T> Specification<T> lessThan(String fieldPath, Double value, int joins) {
        return Objects.isNull(value)
                ? empty()
                : buildLessThanDoubleSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, Double.class));
    }

    public static <T> Specification<T> lessThanOrEqualTo(String fieldPath, Integer value) {
        return lessThanOrEqualTo(fieldPath, value, 0);
    }

    public static <T> Specification<T> lessThanOrEqualTo(String fieldPath, Integer value, int joins) {
        return Objects.isNull(value)
                ? empty()
                : buildLessThanOrEqualToIntegerSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, Integer.class));
    }

    public static <T> Specification<T> lessThanOrEqualTo(String fieldPath, Double value) {
        return lessThanOrEqualTo(fieldPath, value, 0);
    }

    public static <T> Specification<T> lessThanOrEqualTo(String fieldPath, Double value, int joins) {
        return Objects.isNull(value)
                ? empty()
                : buildLessThanOrEqualToDoubleSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, Double.class));
    }

    public static <T> Specification<T> greaterThan(String fieldPath, Integer value) {
        return greaterThan(fieldPath, value, 0);
    }

    public static <T> Specification<T> greaterThan(String fieldPath, Integer value, int joins) {
        return Objects.isNull(value)
                ? empty()
                : buildGreaterThanIntegerSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, Integer.class));
    }

    public static <T> Specification<T> greaterThan(String fieldPath, Double value) {
        return greaterThan(fieldPath, value, 0);
    }

    public static <T> Specification<T> greaterThan(String fieldPath, Double value, int joins) {
        return Objects.isNull(value)
                ? empty()
                : buildGreaterThanDoubleSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, Double.class));
    }

    public static <T> Specification<T> greaterThanOrEqualTo(String fieldPath, Integer value) {
        return greaterThanOrEqualTo(fieldPath, value, 0);
    }

    public static <T> Specification<T> greaterThanOrEqualTo(String fieldPath, Integer value, int joins) {
        return Objects.isNull(value)
                ? empty()
                : buildGreaterThanOrEqualToIntegerSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, Integer.class));
    }

    public static <T> Specification<T> greaterThanOrEqualTo(String fieldPath, Double value) {
        return greaterThanOrEqualTo(fieldPath, value, 0);
    }

    public static <T> Specification<T> greaterThanOrEqualTo(String fieldPath, Double value, int joins) {
        return Objects.isNull(value)
                ? empty()
                : buildGreaterThanOrEqualToDoubleSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, Double.class));
    }

    public static <T, E> Specification<T> equal(String fieldPath, E value, Class<E> clazz) {
        return equal(fieldPath, value, 0, clazz);
    }

    public static <T, E> Specification<T> equal(String fieldPath, E value, int joins, Class<E> clazz) {
        return Objects.isNull(value)
                ? empty()
                : buildEqualSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, clazz));
    }

    public static <T, E> Specification<T> isNull(String fieldPath, Class<E> clazz) {
        return isNull(fieldPath, 0, clazz);
    }

    public static <T, E> Specification<T> isNull(String fieldPath, int joins, Class<E> clazz) {
        return buildIsNullSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, clazz));
    }

    public static <T> Specification<T> contains(String fieldPath, String value) {
        return contains(fieldPath, value, 0);
    }

    public static <T> Specification<T> contains(String fieldPath, String value, int joins) {
        return StringUtils.isBlank(value)
                ? empty()
                : buildContainsSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, String.class));
    }

    public static <T> Specification<T> containsIgnoreCase(String fieldPath, String value) {
        return containsIgnoreCase(fieldPath, value, 0);
    }

    public static <T> Specification<T> containsIgnoreCase(String fieldPath, String value, int joins) {
        return StringUtils.isBlank(value)
                ? empty()
                : buildContainsIgnoreCaseSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, value, String.class));
    }

    public static <T> Specification<T> in(String fieldPath, List<?> values) {
        return in(fieldPath, values, 0);
    }

    public static <T> Specification<T> in(String fieldPath, List<?> values, int joins) {
        return buildInSpecification(CriteriaParamsBuilder.buildCriteriaParams(fieldPath, joins, values, Object.class));
    }

    public static <T> Specification<T> between(String fieldPath, Integer value1, Integer value2) {
        if (Objects.isNull(value1)) {
            return lessThanOrEqualTo(fieldPath, value2);
        } else if (Objects.isNull(value2)) {
            return greaterThanOrEqualTo(fieldPath, value1);
        }
        return (root, query, builder) -> builder.between(root.get(fieldPath), value1, value2);
    }

    public static <T> Specification<T> between(String fieldPath, Double value1, Double value2) {
        if (Objects.isNull(value1)) {
            return lessThanOrEqualTo(fieldPath, value2);
        } else if (Objects.isNull(value2)) {
            return greaterThanOrEqualTo(fieldPath, value1);
        }
        return (root, query, builder) -> builder.between(root.get(fieldPath), value1, value2);
    }

    public static <T> Specification<T> setSizeLessThan(String collectionPath, String fieldPath) {
        return setSizeLessThan(collectionPath, 0, fieldPath, 0);
    }

    public static <T> Specification<T> listSizeLessThan(String collectionPath, String fieldPath) {
        return listSizeLessThan(collectionPath, 0, fieldPath, 0);
    }

    public static <T> Specification<T> listSizeLessThan(String collectionPath,
                                                        int collectionFieldsJoins,
                                                        String fieldPath,
                                                        int fieldJoins) {
        final CriteriaCompareParams<List<Object>, Integer> compareParams = CriteriaParamsBuilder.buildCriteriaCompareListParams(
                collectionPath,
                collectionFieldsJoins,
                fieldPath,
                fieldJoins);
        return buildCollectionSizeLessThanSpecification(compareParams);
    }

    public static <T> Specification<T> setSizeLessThan(String collectionPath,
                                                       int collectionFieldsJoins,
                                                       String fieldPath,
                                                       int fieldJoins) {
        final CriteriaCompareParams<Set<Object>, Integer> criteriaCompareParams = CriteriaParamsBuilder.buildCriteriaCompareSetParams(
                collectionPath,
                collectionFieldsJoins,
                fieldPath,
                fieldJoins);
        return buildCollectionSizeLessThanSpecification(criteriaCompareParams);
    }

    private static <T> Specification<T> buildLessThanIntegerSpecification(CriteriaParams<Integer> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.lessThan(getExpression(params, root), params.getCriteriaValue())
                : empty();
    }

    private static <T> Specification<T> buildLessThanDoubleSpecification(CriteriaParams<Double> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.lessThan(getExpression(params, root), params.getCriteriaValue())
                : empty();
    }

    private static <T> Specification<T> buildGreaterThanIntegerSpecification(CriteriaParams<Integer> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.greaterThan(getExpression(params, root), params.getCriteriaValue())
                : empty();
    }

    private static <T> Specification<T> buildGreaterThanDoubleSpecification(CriteriaParams<Double> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.greaterThan(getExpression(params, root), params.getCriteriaValue())
                : empty();
    }

    private static <T> Specification<T> buildLessThanOrEqualToIntegerSpecification(CriteriaParams<Integer> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.lessThanOrEqualTo(getExpression(params, root), params.getCriteriaValue())
                : empty();
    }

    private static <T> Specification<T> buildLessThanOrEqualToDoubleSpecification(CriteriaParams<Double> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.lessThanOrEqualTo(getExpression(params, root), params.getCriteriaValue())
                : empty();
    }

    private static <T> Specification<T> buildGreaterThanOrEqualToIntegerSpecification(CriteriaParams<Integer> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.greaterThanOrEqualTo(getExpression(params, root), params.getCriteriaValue())
                : empty();
    }

    private static <T> Specification<T> buildGreaterThanOrEqualToDoubleSpecification(CriteriaParams<Double> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.greaterThanOrEqualTo(getExpression(params, root), params.getCriteriaValue())
                : empty();
    }

    private static <T, E> Specification<T> buildIsNullSpecification(CriteriaParams<E> params) {
        return params.hasOperations() ? (root, query, builder) -> builder.isNull(getPath(params, root)) : empty();
    }

    private static <T> Specification<T> buildContainsSpecification(CriteriaParams<String> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.like(getExpression(params, root), "%" + params.getCriteriaValue() + "%")
                : empty();
    }

    private static <T> Specification<T> buildContainsIgnoreCaseSpecification(CriteriaParams<String> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.like(builder.lower(getExpression(params, root)),
                                                         "%" + params.getCriteriaValue().toLowerCase() + "%")
                : empty();
    }

    private static <T> Specification<T> buildInSpecification(CriteriaParams<Object> params) {
        return params.hasOperations()
                ? (root, query, builder) -> builder.in(getPath(params, root)).value(params.getCriteriaValue())
                : empty();
    }

    private static <T, E> Specification<T> buildEqualSpecification(CriteriaParams<E> params) {
        return params.isSuitableForExpression()
                ? (root, query, builder) -> builder.equal(getExpression(params, root), params.getCriteriaValue())
                : empty();
    }

    private static <T, E extends Collection<?>, V extends Integer> Specification<T> buildCollectionSizeLessThanSpecification(
            CriteriaCompareParams<E, V> params) {
        return params.isSuitableForCompareExpression()
                ? (root, query, builder) -> {
            Expression<E> collectionExpression = getExpression(params.getFirst(), root);
            Expression<V> comparableValueExpression = getExpression(params.getSecond(), root);
            return builder.lessThan(builder.size(collectionExpression), comparableValueExpression);
        }
                : empty();
    }
}
