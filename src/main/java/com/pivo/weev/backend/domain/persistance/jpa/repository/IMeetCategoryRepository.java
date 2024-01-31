package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.CategoryJpa;
import java.util.Optional;

public interface IMeetCategoryRepository extends IGenericRepository<Long, CategoryJpa> {

    Optional<CategoryJpa> findByName(String name);
}
