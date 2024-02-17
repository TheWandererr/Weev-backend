package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACCESS_DENIED_ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class WebSocketOperationValidator {

    public void validateSubscription(MeetJpa meet, UserJpa subscriber) {
        if (!meet.getMembers().contains(subscriber) && !Objects.equals(meet.getCreator().getId(), subscriber.getId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }
}
