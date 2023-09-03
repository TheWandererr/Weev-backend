package com.pivo.weev.backend.domain.service;

import static com.pivo.weev.backend.common.utils.CollectionUtils.mapToSet;
import static com.pivo.weev.backend.domain.utils.Constants.Errors.AUTHORIZATION_TOKEN_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getDeviceId;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getUserId;
import static java.util.Objects.isNull;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.dao.model.OAuthTokenDetailsJpa;
import com.pivo.weev.backend.dao.model.common.SequencedPersistable;
import com.pivo.weev.backend.dao.repository.wrapper.OAuthTokenDetailsRepositoryWrapper;
import com.pivo.weev.backend.domain.mapping.OAuthTokenDetailsJpaMapper;
import com.pivo.weev.backend.domain.model.OAuthTokenDetails;
import com.pivo.weev.backend.web.model.auth.LoginDetails;
import com.pivo.weev.backend.common.utils.CollectionUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class OAuthTokenManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(OAuthTokenManager.class);

  private final OAuthTokenDetailsRepositoryWrapper oAuthTokenDetailsRepository;

  public void saveTokenDetails(OAuthTokenDetails tokenDetails) {
    OAuthTokenDetailsJpa tokenDetailsJpa = getMapper(OAuthTokenDetailsJpaMapper.class).map(tokenDetails);
    oAuthTokenDetailsRepository.save(tokenDetailsJpa);
  }

  @Transactional
  public void updateTokenDetails(LoginDetails loginDetails) {
    OAuthTokenDetailsJpa tokenDetails = oAuthTokenDetailsRepository.findByUserIdAndDeviceId(
        loginDetails.getUserId(),
        loginDetails.getDeviceId()
    );
    if (isNull(tokenDetails)) {
      throw new AuthorizationServiceException(AUTHORIZATION_TOKEN_NOT_FOUND_ERROR);
    }
    tokenDetails.setSerial(loginDetails.getSerial());
  }

  @Scheduled(fixedRate = 1800000)
  @Transactional
  public void scheduleOAuthTokensExpiredRemoval() {
    LOGGER.info("Started scheduled job OAuth Tokens expired removal");
    List<OAuthTokenDetailsJpa> allExpired = oAuthTokenDetailsRepository.findAllExpired();
    Set<Long> ids = CollectionUtils.mapToSet(allExpired, SequencedPersistable::getId);
    oAuthTokenDetailsRepository.removeAllByIds(ids);
    LOGGER.info("Finished scheduled job OAuth Tokens expired removal");
  }

  @Transactional
  public void removeTokenDetails(Jwt jwt) {
    oAuthTokenDetailsRepository.removeByUserIdAndDeviceId(getUserId(jwt), getDeviceId(jwt));
  }

}
