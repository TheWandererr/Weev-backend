package com.pivo.weev.backend.domain.service.message;

import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.REQUESTER_NICKNAME;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationHeaders.BODY;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationHeaders.TITLE;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CANCELLATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_NEW_JOIN_REQUEST;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_UPDATE_FAILED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_UPDATE_SUCCESSFUL;
import static com.pivo.weev.backend.utils.CollectionUtils.collect;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToSet;
import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLocale;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.integration.firebase.factory.PushNotificationsFactory;
import com.pivo.weev.backend.integration.firebase.model.PushNotificationMessage;
import com.pivo.weev.backend.integration.firebase.service.FirebaseMessagingService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final FirebaseMessagingService firebaseMessagingService;
    private final PushNotificationsFactory pushNotificationsFactory;

    public void notifyAll(MeetJpa meet, List<DeviceJpa> devices, String topic, Map<String, Object> bodyDetails) {
        Map<UserJpa, List<DeviceJpa>> notifiableDevicesByUser = collect(devices, groupingBy(DeviceJpa::getUser));
        for (List<DeviceJpa> notifiableDevices : notifiableDevicesByUser.values()) {
            collect(notifiableDevices, groupingBy(DeviceJpa::getLang))
                    .forEach((lang, notifiableDevicesByLang) -> {
                        Set<String> notificationTokens = mapToSet(notifiableDevicesByLang, DeviceJpa::getPushNotificationToken);
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
                        firebaseMessagingService.send(message);
                    });
        }
    }

    private Object[] resolveTitleArgs(MeetJpa meet, String topic, Map<String, Object> bodyDetails) {
        return switch (topic) {
            default -> new Object[0];
        };
    }

    private Object[] resolveBodyArgs(MeetJpa meet, String topic, Map<String, Object> bodyDetails) {
        return switch (topic) {
            case MEET_CONFIRMATION -> createMeetConfirmationBodyArgs(meet);
            case MEET_UPDATE_SUCCESSFUL -> createMeetUpdateSuccessfulBodyArgs(meet);
            case MEET_UPDATE_FAILED -> createMeetUpdateFailedBodyArgs(meet);
            case MEET_DECLINATION -> createMeetDeclinationBodyArgs(meet);
            case MEET_CANCELLATION -> createMeetCancellationBodyArgs(meet);
            case MEET_NEW_JOIN_REQUEST -> createNewJoinRequestBodyArgs(meet, bodyDetails);
            case MEET_JOIN_REQUEST_CONFIRMATION -> createJoinRequestConfirmationBodyArgs(meet);
            default -> new Object[0];
        };
    }

    private Object[] createMeetConfirmationBodyArgs(MeetJpa meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetUpdateSuccessfulBodyArgs(MeetJpa meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetUpdateFailedBodyArgs(MeetJpa meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetDeclinationBodyArgs(MeetJpa meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createMeetCancellationBodyArgs(MeetJpa meet) {
        return new Object[]{meet.getHeader()};
    }

    private Object[] createNewJoinRequestBodyArgs(MeetJpa meet, Map<String, Object> bodyDetails) {
        String nickname = bodyDetails.getOrDefault(REQUESTER_NICKNAME, EMPTY).toString();
        return new Object[]{nickname, meet.getHeader()};
    }

    private Object[] createJoinRequestConfirmationBodyArgs(MeetJpa meet) {
        return new Object[]{meet.getHeader()};
    }
}
