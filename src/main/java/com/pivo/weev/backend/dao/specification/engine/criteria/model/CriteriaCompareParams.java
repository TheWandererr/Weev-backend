package com.pivo.weev.backend.dao.specification.engine.criteria.model;

import static java.util.Objects.nonNull;

public class CriteriaCompareParams<E, V> {

  private final CriteriaParams<E> first;
  private final CriteriaParams<V> second;

  public CriteriaCompareParams(CriteriaParams<E> first, CriteriaParams<V> second) {
    this.first = first;
    this.second = second;
  }

  public CriteriaParams<E> getFirst() {
    return first;
  }

  public CriteriaParams<V> getSecond() {
    return second;
  }

  public boolean isSuitableForExpression() {
    return nonNull(first)
        && nonNull(second)
        && first.hasOperations()
        && second.hasOperations()
        && nonNull(first.getCriteriaClass())
        && nonNull(second.getCriteriaClass());
  }

  public boolean isSuitableForCompareExpression() {
    return nonNull(first)
        && nonNull(second)
        && first.hasOperations()
        && second.hasOperations();
  }
}
