package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.DELETED;

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

    public void logicalDelete(EventJpa resource) {
        resource.setPhoto(null);
        resource.setStatus(DELETED);
    }

    @Override
    public void forceDelete(EventJpa resource) {
        resource.setPhoto(null);
        super.forceDeleteById(resource.getId());
    }

    public Optional<EventJpa> findVisibleById(Long id) {
        return repository.findByIdAndDeletedIsFalse(id);
    }
}
