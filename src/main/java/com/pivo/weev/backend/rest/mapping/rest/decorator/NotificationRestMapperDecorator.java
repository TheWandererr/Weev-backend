package com.pivo.weev.backend.rest.mapping.rest.decorator;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.messaging.payload.MeetPayload;
import com.pivo.weev.backend.domain.model.messaging.payload.UserPayload;
import com.pivo.weev.backend.domain.model.user.Notification;
import com.pivo.weev.backend.rest.mapping.rest.MeetSnapshotRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.NotificationRestMapper;
import com.pivo.weev.backend.rest.mapping.rest.UserSnapshotRestMapper;
import com.pivo.weev.backend.rest.model.user.NotificationRest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class NotificationRestMapperDecorator implements NotificationRestMapper {

    private static final MeetSnapshotRestMapper MEET_SNAPSHOT_REST_MAPPER = getMapper(MeetSnapshotRestMapper.class);
    private static final UserSnapshotRestMapper USER_SNAPSHOT_REST_MAPPER = getMapper(UserSnapshotRestMapper.class);

    private final NotificationRestMapper delegate;

    @Override
    public NotificationRest map(Notification source) {
        NotificationRest destination = delegate.map(source);
        source.getDetails().forEach((key, payload) -> {
            if (payload instanceof String) {
                destination.getDetails().put(key, payload);
            } else if (payload instanceof MeetPayload meet) {
                destination.getDetails().put(key, MEET_SNAPSHOT_REST_MAPPER.map(meet));
            } else if (payload instanceof UserPayload user) {
                destination.getDetails().put(key, USER_SNAPSHOT_REST_MAPPER.map(user));
            }
        });
        return destination;
    }
}
