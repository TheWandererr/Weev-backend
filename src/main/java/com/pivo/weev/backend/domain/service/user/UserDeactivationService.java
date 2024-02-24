package com.pivo.weev.backend.domain.service.user;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.meet.MeetOperationsService;
import com.pivo.weev.backend.domain.service.meet.MeetRequestsService;
import com.pivo.weev.backend.domain.service.meet.MeetTemplatesService;
import com.pivo.weev.backend.domain.service.messaging.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeactivationService {

    private final MeetOperationsService meetOperationsService;
    private final MeetTemplatesService meetTemplatesService;
    private final MeetRequestsService meetRequestsService;
    private final ChatService chatService;

    public void deactivate(UserJpa user) {
        Long userId = user.getId();
        user.logicalDeleted();
        meetOperationsService.cancelAll(user, true);
        meetOperationsService.leaveAll(user);
        meetTemplatesService.deleteAllTemplates(userId);
        meetRequestsService.deleteAllRequests(userId);
        chatService.handleUserDeactivation(userId);
    }
}
