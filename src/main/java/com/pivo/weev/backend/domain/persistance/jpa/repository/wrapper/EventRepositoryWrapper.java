package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IEventRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EventRepositoryWrapper extends GenericRepositoryWrapper<Long, EventJpa, IEventRepository> {

    protected EventRepositoryWrapper(IEventRepository repository) {
        super(repository, ResourceName.EVENT);
    }

    public Optional<EventJpa> findByUpdatableTargetId(Long id) {
        return repository.findByUpdatableTargetId(id);
    }

    public void deleteByUpdatableTargetId(Long id) {
        repository.deleteByUpdatableTargetId(id);
    }

    @Override
    public void delete(EventJpa resource) {
        resource.setPhoto(null);
        super.delete(resource);
    }
}
