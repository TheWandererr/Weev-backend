package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACCESS_DENIED_ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.springframework.stereotype.Component;

@Component
public class ChatOperationValidator {

    public void validateSubscription(MeetJpa meet, UserJpa subscriber) {
        if (!meet.getMembersWithCreator().contains(subscriber)) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        /*if (meet.isEnded()) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, null, FORBIDDEN);
        }*/
    }

    public void validateSendMessage(MeetJpa meet, UserJpa sender) {
        if (!meet.getMembersWithCreator().contains(sender)) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        /*if (meet.isEnded()) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, null, FORBIDDEN);
        }*/
    }

    public void validateGetChatMessages(MeetJpa meet, UserJpa requester) {
        if (!meet.getMembersWithCreator().contains(requester)) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }
}
