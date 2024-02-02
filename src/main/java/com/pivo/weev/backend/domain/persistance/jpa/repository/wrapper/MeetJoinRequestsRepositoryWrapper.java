package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.MEET_REQUEST;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJoinRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IMeetJoinRequestsRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class MeetJoinRequestsRepositoryWrapper extends GenericRepositoryWrapper<Long, MeetJoinRequestJpa, IMeetJoinRequestsRepository> {

    protected MeetJoinRequestsRepositoryWrapper(IMeetJoinRequestsRepository repository) {
        super(repository, MEET_REQUEST);
    }

    public Optional<MeetJoinRequestJpa> findByMeetIdAndUserId(Long meetId, Long userId) {
        return repository.findByMeetIdAndUserId(meetId, userId);
    }

    public Page<MeetJoinRequestJpa> findAllByMeetId(Long meetId, Pageable pageable) {
        return repository.findAllByMeet_Id(meetId, pageable);
    }
}
