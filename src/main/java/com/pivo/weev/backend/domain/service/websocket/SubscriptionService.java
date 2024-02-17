package com.pivo.weev.backend.domain.service.websocket;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.rest.utils.Constants.ResponseDetails.TITLE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MEET_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.USER_NOT_FOUND_ERROR;

import com.pivo.weev.backend.domain.model.messaging.chat.SubscriptionMessage;
import com.pivo.weev.backend.domain.persistance.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import com.pivo.weev.backend.domain.service.validation.WebSocketOperationValidator;
import com.pivo.weev.backend.domain.service.websocket.factory.MessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final MeetSearchService meetSearchService;
    private final UserResourceService userResourceService;

    private final MessageFactory messageFactory;

    private final WebSocketOperationValidator operationValidator;

    @Transactional
    public SubscriptionMessage handleSubscription(Long chatId, String nickname) {
        MeetJpa meet = meetSearchService.searchJpa(chatId).orElseThrow(() -> new ResourceNotFoundException(MEET_NOT_FOUND_ERROR + TITLE));
        UserJpa subscriber = userResourceService.findUserJpa(nickname, NICKNAME).orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_ERROR + TITLE));
        operationValidator.validateSubscription(meet, subscriber);
        return messageFactory.createSubscriptionMessage(chatId);
    }
}
