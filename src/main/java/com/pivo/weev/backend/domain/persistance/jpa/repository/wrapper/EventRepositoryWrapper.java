package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IEventRepository;
import org.springframework.stereotype.Component;

@Component
public class EventRepositoryWrapper extends GenericRepositoryWrapper<Long, EventJpa, IEventRepository> {

    protected EventRepositoryWrapper(IEventRepository repository) {
        super(repository, ResourceName.EVENT);
    }
}
