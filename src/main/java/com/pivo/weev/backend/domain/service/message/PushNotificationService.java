package com.pivo.weev.backend.domain.service.message;

import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.REQUESTER_NICKNAME;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationHeaders.BODY;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationHeaders.TITLE;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.CHAT_CREATED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.CHAT_NEW_MESSAGE;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CANCELLATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_NEW_JOIN_REQUEST;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_UPDATE_FAILED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_UPDATE_SUCCESSFUL;
import static com.pivo.weev.backend.utils.CollectionUtils.collect;
import static com.pivo.weev.backend.utils.CollectionUtils.flatMapToList;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToSet;
import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLocale;
import static com.pivo.weev.backend.utils.StreamUtils.select;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.pivo.weev.backend.domain.model.event.payload.DevicePayload;
import com.pivo.weev.backend.domain.model.event.payload.MeetPayload;
import com.pivo.weev.backend.domain.model.event.payload.UserPayload;
import com.pivo.weev.backend.domain.utils.Constants.NotificationDetails;
import com.pivo.weev.backend.integration.firebase.factory.FirebasePushNotificationsFactory;
import com.pivo.weev.backend.integration.firebase.model.notification.FirebasePushNotificationMessage;
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
    private final FirebasePushNotificationsFactory firebasePushNotificationsFactory;

    public void sendPushNotifications(MeetPayload meet, Set<UserPayload> recipients, String topic, Map<String, Object> payload) {
        List<DevicePayload> devices = flatMapToList(recipients, recipient -> select(recipient.getDevices(), DevicePayload::hasPushNotificationToken));
        Map<Long, List<DevicePayload>> notifiableDevicesByUser = collect(devices, groupingBy(DevicePayload::getUserId));
        for (List<DevicePayload> notifiableDevices : notifiableDevicesByUser.values()) {
            collect(notifiableDevices, groupingBy(DevicePayload::getLang))
                    .forEach((lang, notifiableDevicesByLang) -> {
                        Set<String> notificationTokens = mapToSet(notifiableDevicesByLang, DevicePayload::getPushNotificationToken);
                        Object[] titleArgs = resolveTitleArgs(meet, topic, payload);
                        Object[] bodyArgs = resolveBodyArgs(meet, topic, payload);
                        FirebasePushNotificationMessage message = firebasePushNotificationsFactory.build(
                                topic + TITLE,
                                titleArgs,
                                topic + BODY,
                                bodyArgs,
                                payload,
                                notificationTokens,
                                getAcceptedLocale(lang)
                        );
                        firebasePushNotificationService.send(message);
                    });
        }
    }

    private Object[] resolveTitleArgs(MeetPayload meet, String topic, Map<String, Object> payload) {
        return switch (topic) {
            case CHAT_CREATED -> createMeetChatCreatedTitleArgs(meet);
            case CHAT_NEW_MESSAGE -> createMeetChatNewMessageTitleArgs(meet);
            default -> new Object[0];
        };
    }

    private Object[] createMeetChatCreatedTitleArgs(MeetPayload meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetChatNewMessageTitleArgs(MeetPayload meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] resolveBodyArgs(MeetPayload meet, String topic, Map<String, Object> payload) {
        return switch (topic) {
            case MEET_CONFIRMATION -> createMeetConfirmationBodyArgs(meet);
            case MEET_UPDATE_SUCCESSFUL -> createMeetUpdateSuccessfulBodyArgs(meet);
            case MEET_UPDATE_FAILED -> createMeetUpdateFailedBodyArgs(meet);
            case MEET_DECLINATION -> createMeetDeclinationBodyArgs(meet);
            case MEET_CANCELLATION -> createMeetCancellationBodyArgs(meet);
            case MEET_NEW_JOIN_REQUEST -> createNewJoinRequestBodyArgs(meet, payload);
            case MEET_JOIN_REQUEST_CONFIRMATION -> createJoinRequestConfirmationBodyArgs(meet);
            case MEET_JOIN_REQUEST_DECLINATION -> createJoinRequestDeclinationBodyArgs(meet);
            case CHAT_CREATED -> createMeetChatCreatedBodyArgs();
            case CHAT_NEW_MESSAGE -> createMeetChatNewMessageBodyArgs(payload);
            default -> new Object[0];
        };
    }

    private Object[] createMeetConfirmationBodyArgs(MeetPayload meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetUpdateSuccessfulBodyArgs(MeetPayload meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetUpdateFailedBodyArgs(MeetPayload meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetDeclinationBodyArgs(MeetPayload meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetCancellationBodyArgs(MeetPayload meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createNewJoinRequestBodyArgs(MeetPayload meet, Map<String, Object> payload) {
        String nickname = payload.getOrDefault(REQUESTER_NICKNAME, EMPTY).toString();
        return new Object[]{nickname, meet.getHeader()};
    }

    private Object[] createJoinRequestConfirmationBodyArgs(MeetPayload meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createJoinRequestDeclinationBodyArgs(MeetPayload meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetChatCreatedBodyArgs() {
        return new Object[0];
    }

    private Object[] createMeetChatNewMessageBodyArgs(Map<String, Object> payload) {
        return new Object[]{payload.get(NotificationDetails.TEXT)};
    }
}
