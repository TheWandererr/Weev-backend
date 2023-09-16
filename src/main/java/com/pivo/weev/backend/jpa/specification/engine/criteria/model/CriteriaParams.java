package com.pivo.weev.backend.jpa.specification.engine.criteria.model;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.common.utils.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

public class CriteriaParams<E> {

    private List<CriteriaJoin> joins;
    private List<CriteriaGet> gets;
    private E criteriaValue;
    private Class<E> criteriaClass;

    public CriteriaParams() {
    }

    public CriteriaParams(List<CriteriaJoin> joins, List<CriteriaGet> gets) {
        this.joins = joins;
        this.gets = gets;
    }

    public List<CriteriaJoin> getJoins() {
        if (isNull(joins)) {
            joins = new ArrayList<>();
        }
        return joins;
    }

    public List<CriteriaGet> getGets() {
        if (isNull(gets)) {
            gets = new ArrayList<>();
        }
        return gets;
    }

    public E getCriteriaValue() {
        return criteriaValue;
    }

    public Class<E> getCriteriaClass() {
        return criteriaClass;
    }

    public void setJoins(List<CriteriaJoin> joins) {
        this.joins = joins;
    }

    public void setGets(List<CriteriaGet> gets) {
        this.gets = gets;
    }

    public void setCriteriaValue(E criteriaValue) {
        this.criteriaValue = criteriaValue;
    }

    public void setCriteriaClass(Class<E> criteriaClass) {
        this.criteriaClass = criteriaClass;
    }

    public boolean hasOperations() {
        return !CollectionUtils.isEmpty(joins) || !CollectionUtils.isEmpty(gets);
    }

    public boolean hasValue() {
        return nonNull(criteriaValue) && nonNull(criteriaClass);
    }

    public boolean isSuitableForExpression() {
        return hasOperations() && hasValue();
    }
}
