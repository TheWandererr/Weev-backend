package com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model;


import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaJoin extends CriteriaOperation {

    private final JoinType joinType;

    public CriteriaJoin(String fieldName, JoinType joinType) {
        super(fieldName);
        this.joinType = joinType;
    }

    public CriteriaJoin(String fieldName) {
        super(fieldName);
        this.joinType = JoinType.INNER;
    }
}
