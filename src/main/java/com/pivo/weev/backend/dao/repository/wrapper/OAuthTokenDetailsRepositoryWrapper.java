package com.pivo.weev.backend.dao.repository.wrapper;

import static com.pivo.weev.backend.dao.model.common.ResourceName.OAUTH_TOKEN_DETAILS;

import com.pivo.weev.backend.dao.model.auth.OAuthTokenDetailsJpa;
import com.pivo.weev.backend.dao.repository.IOAuthTokenDetailsRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class OAuthTokenDetailsRepositoryWrapper extends GenericRepositoryWrapper<Long, OAuthTokenDetailsJpa, IOAuthTokenDetailsRepository> {

  protected OAuthTokenDetailsRepositoryWrapper(IOAuthTokenDetailsRepository repository) {
    super(repository, OAUTH_TOKEN_DETAILS);
  }

  public OAuthTokenDetailsJpa findByUserIdAndDeviceId(Long userId, String deviceId) {
    return repository.findByUserIdAndDeviceId(userId, deviceId);
  }

  public List<OAuthTokenDetailsJpa> findAllExpired() {
    return repository.findAllByExpiresAtBefore(Instant.now());
  }

  public void removeAllByIds(Set<Long> ids) {
    repository.deleteAllById(ids);
  }

  public void removeByUserIdAndDeviceId(Long userId, String deviceId) {
    repository.deleteByUserIdAndDeviceId(userId, deviceId);
  }
}
