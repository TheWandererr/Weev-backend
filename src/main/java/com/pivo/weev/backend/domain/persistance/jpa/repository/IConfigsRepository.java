package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.Config;
import java.util.Optional;

public interface IConfigsRepository extends IGenericRepository<Long, Config> {

    Optional<Config> findByName(String name);
}
