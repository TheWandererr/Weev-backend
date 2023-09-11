package com.pivo.weev.backend.jpa.repository;

import com.pivo.weev.backend.jpa.model.event.CategoryJpa;
import java.util.Optional;

public interface IEventCategoryRepository extends IGenericRepository<Long, CategoryJpa> {

  Optional<CategoryJpa> findByName(String name);
}
