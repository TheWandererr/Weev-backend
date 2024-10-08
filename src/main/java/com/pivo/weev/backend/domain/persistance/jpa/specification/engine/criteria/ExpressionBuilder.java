package com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria;


import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaGet;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaJoin;
import com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model.CriteriaParams;
import com.pivo.weev.backend.utils.CollectionUtils;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExpressionBuilder {

    public static <E> Expression<E> getExpression(CriteriaParams<E> params, Root<?> root) {
        return getPath(params, root).as(params.getCriteriaClass());
    }

    public static <E> Path<Object> getPath(CriteriaParams<E> params, Root<?> root) {
        int joins = params.getJoins().size();
        return joins == 0 ? buildPath(params.getGets(), root::get) : buildJoinedPath(params, root::join);
    }

    private static <E> Path<Object> buildJoinedPath(CriteriaParams<E> params,
                                                    Function<String, Join<Object, Object>> initialJoinProvider) {
        var joinsChain = buildJoin(params.getJoins(), initialJoinProvider);
        return buildPath(params.getGets(), joinsChain::get);
    }

    private static Join<Object, Object> buildJoin(List<CriteriaJoin> joins, Function<String, Join<Object, Object>> initialJoinProvider) {
        final List<CriteriaJoin> criteriaOperations = new ArrayList<>(joins);
        var joinsChain = initialJoinProvider.apply(CollectionUtils.pop(criteriaOperations).getFieldName());
        for (CriteriaJoin criteriaJoin : criteriaOperations) {
            joinsChain = joinsChain.join(criteriaJoin.getFieldName(), criteriaJoin.getJoinType());
        }
        return joinsChain;
    }

    private static Path<Object> buildPath(List<CriteriaGet> gets, Function<String, Path<Object>> initialPathProvider) {
        final List<CriteriaGet> criteriaOperations = new ArrayList<>(gets);
        var path = initialPathProvider.apply(CollectionUtils.pop(criteriaOperations).getFieldName());
        for (CriteriaGet criteriaOperation : criteriaOperations) {
            path = path.get(criteriaOperation.getFieldName());
        }
        return path;
    }
}
