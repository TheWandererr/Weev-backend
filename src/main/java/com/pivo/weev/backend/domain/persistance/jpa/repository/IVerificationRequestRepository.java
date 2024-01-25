package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.VerificationRequestJpa;
import java.util.Optional;

public interface IVerificationRequestRepository extends IGenericRepository<Long, VerificationRequestJpa> {

    Optional<VerificationRequestJpa> findByEmail(String email);
}
