package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.ON_MODERATION;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IMeetRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class MeetRepository extends GenericRepository<Long, MeetJpa, IMeetRepository> {

    protected MeetRepository(IMeetRepository repository) {
        super(repository, ResourceName.MEET);
    }

    public Optional<MeetJpa> findByUpdatableTargetId(Long id) {
        return repository.findByUpdatableTargetId(id);
    }

    public void deleteByUpdatableTargetId(Long id) {
        repository.deleteByUpdatableTargetId(id);
    }

    public void deleteAllByUpdatableTargetId(Set<Long> ids) {
        repository.deleteAllByUpdatableTargetId(ids);
    }

    @Override
    public void forceDelete(MeetJpa resource) {
        resource.setPhoto(null);
        super.forceDeleteById(resource.getId());
    }

    public Optional<MeetJpa> findById(Long id) {
        return repository.findById(id);
    }

    public List<MeetJpa> findAllPublishedActiveByCreatorId(Long creatorId) {
        return repository.findAllByCreator_IdAndUtcEndDateTimeAfterAndStatusIn(creatorId, Instant.now(), List.of(CONFIRMED, HAS_MODERATION_INSTANCE, ON_MODERATION));
    }

    public List<MeetJpa> findAllActiveByMemberId(Long memberId) {
        return repository.findAllByMember_IdAndUtcEndDateTimeAfter(memberId, Instant.now());
    }
}
