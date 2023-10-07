package com.pivo.weev.backend.domain.service.config;

import static com.pivo.weev.backend.domain.persistance.jpa.utils.Constants.ConfigNames.IMAGE_COMPRESSING_SCALE;
import static java.lang.Long.parseLong;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.Config;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.ConfigsRepositoryWrapper;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigsWrapper {

    private final ConfigsRepositoryWrapper configsRepository;

    public Map<Long, Double> getImageCompressingScale() {
        Map<Long, Double> result = new TreeMap<>();
        Config config = configsRepository.findByName(IMAGE_COMPRESSING_SCALE).orElse(new Config());
        config.getMap().forEach((size, scale) -> result.put(parseLong(size), (Double) scale));
        return result;
    }
}
