package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.CONFIG;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.Config;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IConfigsRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConfigsRepositoryWrapper extends GenericRepositoryWrapper<Long, Config, IConfigsRepository> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigsRepositoryWrapper.class);

    protected ConfigsRepositoryWrapper(IConfigsRepository repository) {
        super(repository, CONFIG);
    }

    public Optional<Config> findByName(String name) {
        Optional<Config> config = repository.findByName(name);
        if (config.isEmpty()) {
            LOGGER.warn("config not found : {}", name);
        }
        return config;
    }
}
