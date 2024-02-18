package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACCESS_DENIED_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class ChatOperationValidator {

    public void validateSubscription(MeetJpa meet, UserJpa subscriber) {
        if (!meet.getMembers().contains(subscriber) && !Objects.equals(meet.getCreator().getId(), subscriber.getId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (meet.isEnded()) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, null, FORBIDDEN);
        }
    }

    public void validateSendMessage(MeetJpa meet, UserJpa sender) {
        if (!meet.getMembers().contains(sender) && !Objects.equals(meet.getCreator().getId(), sender.getId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (meet.isEnded()) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, null, FORBIDDEN);
        }
    }
}
