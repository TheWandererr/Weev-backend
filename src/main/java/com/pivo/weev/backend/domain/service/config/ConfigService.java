package com.pivo.weev.backend.domain.service.config;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.Configs.ACCESS_TOKEN_EXPIRES_AMOUNT;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Configs.IMAGE_COMPRESSING_SCALE;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Configs.REFRESH_TOKEN_EXPIRES_AMOUNT;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Configs.VERIFICATION_REQUEST_VALIDITY_PERIOD;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Firestore.Collections.CONFIGS;
import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentSnapshot;
import com.pivo.weev.backend.domain.persistance.document.Config;
import com.pivo.weev.backend.domain.persistance.document.ValidityPeriod;
import com.pivo.weev.backend.integration.firebase.client.FirestoreClient;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigService {

    private final FirestoreClient firestoreClient;
    private final ObjectMapper mapper;
    private final ApplicationLoggingHelper loggingHelper;

    public Map<Long, Double> getImageCompressingScale() {
        Map<Long, Double> result = new TreeMap<>();
        Config config = findConfig(IMAGE_COMPRESSING_SCALE).orElse(new Config());
        config.getMap().forEach((size, scale) -> result.put(parseLong(size), parseDouble(scale.toString())));
        return result;
    }

    public Integer getAccessTokenExpiresAmount() {
        return findConfig(ACCESS_TOKEN_EXPIRES_AMOUNT)
                .map(Config::getInteger)
                .orElse(1);
    }

    public Integer getRefreshTokenExpiresAmount() {
        return findConfig(REFRESH_TOKEN_EXPIRES_AMOUNT)
                .map(Config::getInteger)
                .orElse(168);
    }

    public ValidityPeriod getVerificationRequestValidityPeriod() {
        return findConfig(VERIFICATION_REQUEST_VALIDITY_PERIOD)
                .map(Config::getMap)
                .map(map -> mapper.convertValue(map, ValidityPeriod.class))
                .orElse(new ValidityPeriod());
    }

    private Optional<Config> findConfig(String name) {
        DocumentSnapshot snapshot = firestoreClient.getSnapshot(CONFIGS, name);
        try {
            return Optional.of(mapper.convertValue(snapshot.getData(), Config.class));
        } catch (Exception exception) {
            log.error(loggingHelper.buildLoggingError(exception, null, false));
            return Optional.empty();
        }
    }
}
