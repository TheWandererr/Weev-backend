package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.utils.CollectionUtils.mapToSet;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.auth.AuthTokens;
import com.pivo.weev.backend.domain.model.auth.LoginDetails;
import com.pivo.weev.backend.domain.persistance.jpa.model.auth.AuthTokensDetailsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.SequencedPersistable;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.AuthTokensDetailsRepository;
import com.pivo.weev.backend.domain.service.user.DeviceService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class AuthTokensDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokensDetailsService.class);

    private final AuthTokensDetailsRepository authTokenDetailsRepository;

    private final DeviceService deviceService;

    @Transactional
    public AuthTokensDetailsJpa createTokensDetails(LoginDetails loginDetails, AuthTokens authTokens) {
        DeviceJpa device = deviceService.resolveDevice(loginDetails.user(), loginDetails.deviceId());
        AuthTokensDetailsJpa tokenDetails = AuthTokensDetailsJpa.builder()
                                                                .device(device)
                                                                .serial(loginDetails.serial())
                                                                .expiresAt(authTokens.getRefreshToken().getExpiresAt())
                                                                .build();
        return authTokenDetailsRepository.save(tokenDetails);
    }

    @Transactional
    public boolean updateTokensDetails(LoginDetails loginDetails, AuthTokens authTokens) {
        AuthTokensDetailsJpa tokenDetails = authTokenDetailsRepository.findByUserIdAndDeviceId(loginDetails.getUserId(), loginDetails.deviceId());
        if (isNull(tokenDetails)) {
            return false;
        }
        tokenDetails.setSerial(loginDetails.serial());
        tokenDetails.setExpiresAt(authTokens.getRefreshToken().getExpiresAt());
        return true;
    }

    @Scheduled(fixedRate = 1800000)
    @Transactional
    public void scheduleAuthTokensExpiredRemoval() {
        LOGGER.info("Started scheduled job Auth Tokens expired removal");
        List<AuthTokensDetailsJpa> allExpired = authTokenDetailsRepository.findAllExpired();
        Set<Long> ids = mapToSet(allExpired, SequencedPersistable::getId);
        authTokenDetailsRepository.deleteAllByIds(ids);
        LOGGER.info("Finished scheduled job Auth Tokens expired removal");
    }

    public void revokeTokensDetails(Long userId, String deviceId) {
        authTokenDetailsRepository.deleteByUserIdAndDeviceId(userId, deviceId);
    }

    public void revokeTokensDetails(Long userId) {
        authTokenDetailsRepository.deleteAllByUserId(userId);
    }
}
