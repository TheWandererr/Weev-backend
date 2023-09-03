package com.pivo.weev.backend.dao.specification.engine.criteria.model;

import java.io.Serializable;

public abstract class CriteriaOperation implements Serializable {

  private final String fieldName;

  protected CriteriaOperation(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

}
