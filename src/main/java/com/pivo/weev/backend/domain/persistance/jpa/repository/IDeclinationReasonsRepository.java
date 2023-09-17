package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import java.util.Optional;

public interface IDeclinationReasonsRepository extends IGenericRepository<Long, DeclinationReason> {

    Optional<DeclinationReason> findByTitle(String title);
}
