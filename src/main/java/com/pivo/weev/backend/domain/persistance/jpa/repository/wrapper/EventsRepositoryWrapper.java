package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IEventRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EventsRepositoryWrapper extends GenericRepositoryWrapper<Long, EventJpa, IEventRepository> {

    protected EventsRepositoryWrapper(IEventRepository repository) {
        super(repository, ResourceName.EVENT);
    }

    public Optional<EventJpa> findByUpdatableTargetId(Long id) {
        return repository.findByUpdatableTargetId(id);
    }

    public void deleteByUpdatableTargetId(Long id) {
        repository.deleteByUpdatableTargetId(id);
    }

    @Override
    public void forceDelete(EventJpa resource) {
        resource.setPhoto(null);
        super.forceDeleteById(resource.getId());
    }

    public Optional<EventJpa> findById(Long id) {
        return repository.findById(id);
    }
}
