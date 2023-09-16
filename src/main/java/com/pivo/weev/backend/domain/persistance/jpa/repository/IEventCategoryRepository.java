package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.CategoryJpa;
import java.util.Optional;

public interface IEventCategoryRepository extends IGenericRepository<Long, CategoryJpa> {

    Optional<CategoryJpa> findByName(String name);
}
