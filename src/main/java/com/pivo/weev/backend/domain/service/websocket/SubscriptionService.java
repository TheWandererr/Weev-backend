package com.pivo.weev.backend.domain.service.websocket;

import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.persistance.utils.Constants.FirebaseFirestore.ChatPrefixes.GROUP;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.meet.MeetSearchService;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import com.pivo.weev.backend.domain.service.validation.ChatOperationValidator;
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

    private final ChatOperationValidator operationValidator;

    @Transactional
    public EventMessage handleGroupChatSubscription(String chatId, String nickname) {
        long meetId = toLong(substringAfter(chatId, GROUP));
        MeetJpa meet = meetSearchService.fetchJpa(meetId);
        UserJpa subscriber = userResourceService.fetchJpa(nickname, NICKNAME);
        operationValidator.validateSubscription(meet, subscriber);
        return messageFactory.createSubscriptionMessage(chatId);
    }
}
