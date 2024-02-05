package com.pivo.weev.backend.domain.service.user;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepository;
import com.pivo.weev.backend.domain.service.meet.MeetOperationsService;
import com.pivo.weev.backend.domain.service.meet.MeetRequestsService;
import com.pivo.weev.backend.domain.service.meet.MeetTemplatesService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeactivationService {

    private final MeetOperationsService meetOperationsService;
    private final MeetTemplatesService meetTemplatesService;
    private final MeetRequestsService meetRequestsService;

    private final MeetRepository meetRepository;

    public void deactivate(UserJpa user) {
        Long userId = user.getId();
        user.logicalDeleted();
        List<MeetJpa> cancellableMeets = meetRepository.findAllPublishedActiveByCreatorId(userId);
        meetOperationsService.cancelAll(cancellableMeets, true);
        List<MeetJpa> memberMeets = meetRepository.findAllActiveByMemberId(userId);
        meetOperationsService.leaveAll(memberMeets, user, true);
        meetTemplatesService.deleteAllTemplates(userId);
        meetRequestsService.deleteAllRequests(userId);
    }
}
