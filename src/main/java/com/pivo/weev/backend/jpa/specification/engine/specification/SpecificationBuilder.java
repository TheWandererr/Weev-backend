package com.pivo.weev.backend.jpa.specification.engine.specification;

import com.pivo.weev.backend.common.utils.ArrayUtils;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilder<T> {

  private Specification<T> resultSpecification;

  public SpecificationBuilder() {
    this.resultSpecification = SimpleSpecifications.empty();
  }

  public Specification<T> build() {
    return resultSpecification;
  }

  public SpecificationBuilder<T> and(Specification<T> specification) {
    this.resultSpecification = this.resultSpecification.and(specification);
    return this;
  }

  public SpecificationBuilder<T> or(Specification<T> specification) {
    this.resultSpecification = this.resultSpecification.or(specification);
    return this;
  }

  @SuppressWarnings("unchecked")
  public SpecificationBuilder<T> andAll(Specification<T>... specifications) {
    if (ArrayUtils.isEmpty(specifications)) {
      return this;
    }
    for (Specification<T> specification : specifications) {
      this.resultSpecification = this.resultSpecification.and(specification);
    }
    return this;
  }

  @SuppressWarnings("unchecked")
  public SpecificationBuilder<T> andAny(Specification<T>... specifications) {
    if (ArrayUtils.isEmpty(specifications)) {
      return this;
    }
    Specification<T> inner = SimpleSpecifications.empty();
    for (Specification<T> specification : specifications) {
      inner = inner.or(specification);
    }
    this.resultSpecification = this.resultSpecification.and(inner);
    return this;
  }

  @SuppressWarnings("unchecked")
  public SpecificationBuilder<T> orAll(Specification<T>... specifications) {
    if (ArrayUtils.isEmpty(specifications)) {
      return this;
    }
    Specification<T> inner = SimpleSpecifications.empty();
    for (Specification<T> specification : specifications) {
      inner = inner.and(specification);
    }
    this.resultSpecification = this.resultSpecification.or(inner);
    return this;
  }

  @SuppressWarnings("unchecked")
  public SpecificationBuilder<T> orAny(Specification<T>... specifications) {
    if (ArrayUtils.isEmpty(specifications)) {
      return this;
    }
    for (Specification<T> specification : specifications) {
      this.resultSpecification = this.resultSpecification.or(specification);
    }
    return this;
  }

  public SpecificationBuilder<T> andLessThan(String fieldPath, Integer value) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.lessThan(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> andLessThan(String fieldPath, Integer value, int joins) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.lessThan(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> orLessThan(String fieldPath, Integer value) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.lessThan(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> orLessThan(String fieldPath, Integer value, int joins) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.lessThan(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> andLessThanOrEqualTo(String fieldPath, Integer value) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.lessThanOrEqualTo(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> andLessThanOrEqualTo(String fieldPath, Integer value, int joins) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.lessThanOrEqualTo(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> orLessThanOrEqualTo(String fieldPath, Integer value) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.lessThanOrEqualTo(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> orLessThanOrEqualTo(String fieldPath, Integer value, int joins) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.lessThanOrEqualTo(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> andGreaterThan(String fieldPath, Integer value) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.greaterThan(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> andGreaterThan(String fieldPath, Integer value, int joins) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.greaterThan(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> orGreaterThan(String fieldPath, Integer value) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.greaterThan(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> orGreaterThan(String fieldPath, Integer value, int joins) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.greaterThan(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> andGreaterThanOrEqualTo(String fieldPath, Integer value) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.greaterThanOrEqualTo(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> andGreaterThanOrEqualTo(String fieldPath, Integer value, int joins) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.greaterThanOrEqualTo(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> orGreaterThanOrEqualTo(String fieldPath, Integer value) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.greaterThanOrEqualTo(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> orGreaterThanOrEqualTo(String fieldPath, Integer value, int joins) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.greaterThanOrEqualTo(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> andBetween(String fieldPath, Integer value1, Integer value2) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.between(fieldPath, value1, value2));
    return this;
  }

  public SpecificationBuilder<T> orBetween(String fieldPath, Integer value1, Integer value2) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.between(fieldPath, value1, value2));
    return this;
  }

  public <E> SpecificationBuilder<T> andEqual(String fieldPath, E value, Class<E> clazz) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.equal(fieldPath, value, clazz));
    return this;
  }

  public <E> SpecificationBuilder<T> andEqual(String fieldPath, E value, int joins, Class<E> clazz) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.equal(fieldPath, value, joins, clazz));
    return this;
  }

  public <E> SpecificationBuilder<T> orEqual(String fieldPath, E value, Class<E> clazz) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.equal(fieldPath, value, clazz));
    return this;
  }

  public <E> SpecificationBuilder<T> orEqual(String fieldPath, E value, int joins, Class<E> clazz) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.equal(fieldPath, value, joins, clazz));
    return this;
  }

  public <E> SpecificationBuilder<T> andIsNull(String fieldPath, Class<E> clazz) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.isNull(fieldPath, clazz));
    return this;
  }

  public <E> SpecificationBuilder<T> andIsNull(String fieldPath, int joins, Class<E> clazz) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.isNull(fieldPath, joins, clazz));
    return this;
  }

  public <E> SpecificationBuilder<T> orIsNull(String fieldPath, Class<E> clazz) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.isNull(fieldPath, clazz));
    return this;
  }

  public <E> SpecificationBuilder<T> orIsNull(String fieldPath, int joins, Class<E> clazz) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.isNull(fieldPath, joins, clazz));
    return this;
  }

  public SpecificationBuilder<T> andContains(String fieldPath, String value) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.contains(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> andContains(String fieldPath, String value, int joins) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.contains(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> andContainsIgnoreCase(String fieldPath, String value) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.containsIgnoreCase(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> andContainsIgnoreCase(String fieldPath, String value, int joins) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.containsIgnoreCase(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> orContains(String fieldPath, String value) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.contains(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> orContains(String fieldPath, String value, int joins) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.contains(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> orContainsIgnoreCase(String fieldPath, String value) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.containsIgnoreCase(fieldPath, value));
    return this;
  }

  public SpecificationBuilder<T> orContainsIgnoreCase(String fieldPath, String value, int joins) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.containsIgnoreCase(fieldPath, value, joins));
    return this;
  }

  public SpecificationBuilder<T> andIn(String fieldPath, List<?> values) {
    resultSpecification = resultSpecification.and(SimpleSpecifications.in(fieldPath, values));
    return this;
  }

  public SpecificationBuilder<T> orIn(String fieldPath, List<?> values) {
    resultSpecification = resultSpecification.or(SimpleSpecifications.in(fieldPath, values));
    return this;
  }
}
