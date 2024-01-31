package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.DeclinationReasonJpa;
import java.util.Optional;

public interface IDeclinationReasonsRepository extends IGenericRepository<Long, DeclinationReasonJpa> {

    Optional<DeclinationReasonJpa> findByTitle(String title);
}
