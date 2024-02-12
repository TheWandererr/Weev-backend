package com.pivo.weev.backend.domain.service.message;

import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.REQUESTER_NICKNAME;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationHeaders.BODY;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationHeaders.TITLE;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CANCELLATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CHAT_CREATED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CHAT_NEW_MESSAGE;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_NEW_JOIN_REQUEST;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_UPDATE_FAILED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_UPDATE_SUCCESSFUL;
import static com.pivo.weev.backend.utils.CollectionUtils.collect;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToSet;
import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLocale;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.user.Device;
import com.pivo.weev.backend.domain.utils.Constants.NotificationDetails;
import com.pivo.weev.backend.integration.firebase.factory.PushNotificationsFactory;
import com.pivo.weev.backend.integration.firebase.model.notification.PushNotificationMessage;
import com.pivo.weev.backend.integration.firebase.service.FirebasePushNotificationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final FirebasePushNotificationService firebasePushNotificationService;
    private final PushNotificationsFactory pushNotificationsFactory;

    public void notifyAll(Meet meet, List<Device> devices, String topic, Map<String, Object> bodyDetails) {
        Map<Long, List<Device>> notifiableDevicesByUser = collect(devices, groupingBy(Device::getUserId));
        for (List<Device> notifiableDevices : notifiableDevicesByUser.values()) {
            collect(notifiableDevices, groupingBy(Device::getLang))
                    .forEach((lang, notifiableDevicesByLang) -> {
                        Set<String> notificationTokens = mapToSet(notifiableDevicesByLang, Device::getPushNotificationToken);
                        Object[] titleArgs = resolveTitleArgs(meet, topic, bodyDetails);
                        Object[] bodyArgs = resolveBodyArgs(meet, topic, bodyDetails);
                        PushNotificationMessage message = pushNotificationsFactory.build(
                                topic + TITLE,
                                titleArgs,
                                topic + BODY,
                                bodyArgs,
                                bodyDetails,
                                notificationTokens,
                                getAcceptedLocale(lang)
                        );
                        firebasePushNotificationService.send(message);
                    });
        }
    }

    private Object[] resolveTitleArgs(Meet meet, String topic, Map<String, Object> bodyDetails) {
        return switch (topic) {
            case MEET_CHAT_CREATED -> createMeetChatCreatedTitleArgs(meet);
            case MEET_CHAT_NEW_MESSAGE -> createMeetChatNewMessageTitleArgs(meet);
            default -> new Object[0];
        };
    }

    private Object[] createMeetChatCreatedTitleArgs(Meet meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetChatNewMessageTitleArgs(Meet meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] resolveBodyArgs(Meet meet, String topic, Map<String, Object> bodyDetails) {
        return switch (topic) {
            case MEET_CONFIRMATION -> createMeetConfirmationBodyArgs(meet);
            case MEET_UPDATE_SUCCESSFUL -> createMeetUpdateSuccessfulBodyArgs(meet);
            case MEET_UPDATE_FAILED -> createMeetUpdateFailedBodyArgs(meet);
            case MEET_DECLINATION -> createMeetDeclinationBodyArgs(meet);
            case MEET_CANCELLATION -> createMeetCancellationBodyArgs(meet);
            case MEET_NEW_JOIN_REQUEST -> createNewJoinRequestBodyArgs(meet, bodyDetails);
            case MEET_JOIN_REQUEST_CONFIRMATION -> createJoinRequestConfirmationBodyArgs(meet);
            case MEET_JOIN_REQUEST_DECLINATION -> createJoinRequestDeclinationBodyArgs(meet);
            case MEET_CHAT_CREATED -> createMeetChatCreatedBodyArgs();
            case MEET_CHAT_NEW_MESSAGE -> createMeetChatNewMessageBodyArgs(bodyDetails);
            default -> new Object[0];
        };
    }

    private Object[] createMeetConfirmationBodyArgs(Meet meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetUpdateSuccessfulBodyArgs(Meet meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetUpdateFailedBodyArgs(Meet meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetDeclinationBodyArgs(Meet meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetCancellationBodyArgs(Meet meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createNewJoinRequestBodyArgs(Meet meet, Map<String, Object> bodyDetails) {
        String nickname = bodyDetails.getOrDefault(REQUESTER_NICKNAME, EMPTY).toString();
        return new Object[]{nickname, meet.getHeader()};
    }

    private Object[] createJoinRequestConfirmationBodyArgs(Meet meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createJoinRequestDeclinationBodyArgs(Meet meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetChatCreatedBodyArgs() {
        return new Object[0];
    }

    private Object[] createMeetChatNewMessageBodyArgs(Map<String, Object> bodyDetails) {
        return new Object[]{bodyDetails.get(NotificationDetails.TEXT)};
    }
}
