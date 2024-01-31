package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.MEET_REQUEST;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IMeetRequestsRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MeetRequestsRepositoryWrapper extends GenericRepositoryWrapper<Long, MeetRequestJpa, IMeetRequestsRepository> {

    protected MeetRequestsRepositoryWrapper(IMeetRequestsRepository repository) {
        super(repository, MEET_REQUEST);
    }

    public Optional<MeetRequestJpa> findByMeetIdAndUserId(Long meetId, Long userId) {
        return repository.findByMeetIdAndUserId(meetId, userId);
    }
}
