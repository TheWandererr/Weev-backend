package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.AuthTokensDetailsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IAuthTokenDetailsRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AuthTokensDetailsRepository extends GenericRepository<Long, AuthTokensDetailsJpa, IAuthTokenDetailsRepository> {

    protected AuthTokensDetailsRepository(IAuthTokenDetailsRepository repository) {
        super(repository, ResourceName.AUTH_TOKEN_DETAILS);
    }

    public AuthTokensDetailsJpa findByUserIdAndDeviceInternalId(Long userId, String deviceId) {
        return repository.findByUserIdAndDeviceInternalId(userId, deviceId);
    }

    public List<AuthTokensDetailsJpa> findAllExpired() {
        return repository.findAllByExpiresAtBefore(Instant.now());
    }

    public void deleteAllByIds(Set<Long> ids) {
        repository.deleteAllById(ids);
    }

    public void deleteByUserIdAndDeviceId(Long userId, String deviceId) {
        repository.deleteByUserIdAndDeviceInternalId(userId, deviceId);
    }

    public void deleteAllByUserId(Long userId) {
        repository.deleteAllByUserId(userId);
    }
}
