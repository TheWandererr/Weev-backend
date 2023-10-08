package com.pivo.weev.backend.domain.persistance.jpa.specification.engine.criteria.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class CriteriaOperation implements Serializable {

    private final String fieldName;

}
