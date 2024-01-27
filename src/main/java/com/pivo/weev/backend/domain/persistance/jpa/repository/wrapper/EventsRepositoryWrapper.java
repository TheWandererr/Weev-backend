package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IEventsRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EventsRepositoryWrapper extends GenericRepositoryWrapper<Long, EventJpa, IEventsRepository> {

    protected EventsRepositoryWrapper(IEventsRepository repository) {
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
