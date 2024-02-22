package com.pivo.weev.backend.domain.mapping.domain.decorator;

import static com.pivo.weev.backend.domain.utils.Constants.MessagingPayload.DECLINATION_REASON;
import static com.pivo.weev.backend.domain.utils.Constants.MessagingPayload.MEET;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MeetPayloadMapper;
import com.pivo.weev.backend.domain.mapping.domain.NotificationMapper;
import com.pivo.weev.backend.domain.model.user.Notification;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetNotificationJpa;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class NotificationMapperDecorator implements NotificationMapper {

    private static final MeetPayloadMapper MEET_PAYLOAD_MAPPER = getMapper(MeetPayloadMapper.class);
    private final NotificationMapper delegate;

    @Override
    public Notification map(NotificationJpa source) {
        Notification destination = delegate.map(source);
        destination.setDetails(source.getDetails());
        if (source instanceof MeetNotificationJpa meetNotification) {
            destination.getDetails().putAll(Map.of(
                    DECLINATION_REASON, meetNotification.getDeclinationReasonJpa().getTitle(),
                    MEET, MEET_PAYLOAD_MAPPER.map(meetNotification.getMeet()))
            );
        }
        return destination;
    }

}
