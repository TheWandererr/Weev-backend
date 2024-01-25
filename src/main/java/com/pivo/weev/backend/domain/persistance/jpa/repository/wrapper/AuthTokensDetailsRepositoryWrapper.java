package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.AuthTokensDetailsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IAuthTokenDetailsRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class AuthTokensDetailsRepositoryWrapper extends GenericRepositoryWrapper<Long, AuthTokensDetailsJpa, IAuthTokenDetailsRepository> {

    protected AuthTokensDetailsRepositoryWrapper(IAuthTokenDetailsRepository repository) {
        super(repository, ResourceName.AUTH_TOKEN_DETAILS);
    }

    public AuthTokensDetailsJpa findByUserIdAndDeviceId(Long userId, String deviceId) {
        return repository.findByUserIdAndDeviceId(userId, deviceId);
    }

    public List<AuthTokensDetailsJpa> findAllExpired() {
        return repository.findAllByExpiresAtBefore(Instant.now());
    }

    public void removeAllByIds(Set<Long> ids) {
        repository.deleteAllById(ids);
    }

    public void removeByUserIdAndDeviceId(Long userId, String deviceId) {
        repository.deleteByUserIdAndDeviceId(userId, deviceId);
    }
}
