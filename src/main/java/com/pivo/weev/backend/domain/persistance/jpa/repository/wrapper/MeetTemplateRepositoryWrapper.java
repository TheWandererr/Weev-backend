package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.MEET_TEMPLATE;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetTemplateJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IMeetTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class MeetTemplateRepositoryWrapper extends GenericRepositoryWrapper<Long, MeetTemplateJpa, IMeetTemplateRepository> {

    protected MeetTemplateRepositoryWrapper(IMeetTemplateRepository repository) {
        super(repository, MEET_TEMPLATE);
    }

    public Page<MeetTemplateJpa> findAllByUserId(Long userId, Pageable pageable) {
        return repository.findAllByCreator_Id(userId, pageable);
    }

    public void forceDeleteAllByCreatorId(Long userId) {
        repository.deleteAllByCreatorId(userId);
    }
}
