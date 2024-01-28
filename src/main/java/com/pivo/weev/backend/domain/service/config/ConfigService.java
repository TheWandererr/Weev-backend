package com.pivo.weev.backend.domain.service.config;

import static com.pivo.weev.backend.domain.persistance.utils.Constants.Configs.ACCESS_TOKEN_EXPIRES_AMOUNT;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Configs.IMAGE_COMPRESSING_SCALE;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Configs.REFRESH_TOKEN_EXPIRES_AMOUNT;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.Configs.VERIFICATION_REQUEST_VALIDITY_PERIOD;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import com.google.firebase.remoteconfig.Parameter;
import com.google.firebase.remoteconfig.ParameterValue.Explicit;
import com.google.gson.Gson;
import com.pivo.weev.backend.config.model.ValidityPeriod;
import com.pivo.weev.backend.integration.firebase.client.RemoteConfigClient;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigService {

    private final RemoteConfigClient remoteConfigClient;
    private final Gson gson;
    private final ApplicationLoggingHelper loggingHelper;

    @SuppressWarnings("unchecked")
    public Map<Long, Double> getImageCompressingScale() {
        return findExplicit(IMAGE_COMPRESSING_SCALE)
                .map(Explicit::getValue)
                .map(string -> (Map<String, Double>) gson.fromJson(string, Map.class))
                .stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(toMap(entry -> toLong(entry.getKey()), Entry::getValue, (v1, v2) -> v1, TreeMap::new));
    }

    public Integer getAccessTokenExpiresAmount() {
        return findExplicit(ACCESS_TOKEN_EXPIRES_AMOUNT)
                .map(Explicit::getValue)
                .map(NumberUtils::toInt)
                .orElse(1);
    }

    public Integer getRefreshTokenExpiresAmount() {
        return findExplicit(REFRESH_TOKEN_EXPIRES_AMOUNT)
                .map(Explicit::getValue)
                .map(NumberUtils::toInt)
                .orElse(168);
    }

    public ValidityPeriod getVerificationRequestValidityPeriod() {
        return findExplicit(VERIFICATION_REQUEST_VALIDITY_PERIOD)
                .map(Explicit::getValue)
                .map(json -> gson.fromJson(json, ValidityPeriod.class))
                .orElse(new ValidityPeriod());
    }

    private Optional<Explicit> findExplicit(String key) {
        Parameter parameter = remoteConfigClient.getParameter(key);
        if (isNull(parameter)) {
            return Optional.empty();
        }
        try {
            return Optional.of((Explicit) parameter.getDefaultValue());
        } catch (Exception exception) {
            log.error(loggingHelper.buildLoggingError(exception, null, false));
            return Optional.empty();
        }
    }
}
