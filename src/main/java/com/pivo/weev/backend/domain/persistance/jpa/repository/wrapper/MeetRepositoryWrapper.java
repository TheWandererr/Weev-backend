package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IMeetRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MeetRepositoryWrapper extends GenericRepositoryWrapper<Long, MeetJpa, IMeetRepository> {

    protected MeetRepositoryWrapper(IMeetRepository repository) {
        super(repository, ResourceName.MEET);
    }

    public Optional<MeetJpa> findByUpdatableTargetId(Long id) {
        return repository.findByUpdatableTargetId(id);
    }

    public void deleteByUpdatableTargetId(Long id) {
        repository.deleteByUpdatableTargetId(id);
    }

    @Override
    public void forceDelete(MeetJpa resource) {
        resource.setPhoto(null);
        super.forceDeleteById(resource.getId());
    }

    public Optional<MeetJpa> findById(Long id) {
        return repository.findById(id);
    }
}
