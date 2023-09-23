package com.pivo.weev.backend.domain.utils;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getNullableUserId;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.event.Event;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class EventDataUtils {

    public static boolean isHidePrivateData(Event source) {
        Long viewerId = getNullableUserId();
        return source.getRestrictions().isJoinByRequest()
                && (isNull(viewerId) || !Objects.equals(source.getCreator().getId(), viewerId) || !source.hasMember(viewerId));
    }
}
