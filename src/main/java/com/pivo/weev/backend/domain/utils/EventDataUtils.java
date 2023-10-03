package com.pivo.weev.backend.domain.utils;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getNullableUserId;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.event.Event;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class EventDataUtils {

    public static boolean hasHiddenData(Event source) {
        if (!source.hasRestrictions()) {
            return false;
        }
        Long viewerId = getNullableUserId();
        if (source.getRestrictions().isJoinByRequest()) {
            boolean unauthorized = isNull(viewerId);
            boolean isMemberOrCreator = Objects.equals(source.getCreator().getId(), viewerId) || source.hasMember(viewerId);
            return unauthorized || !isMemberOrCreator;
        }
        return false;
    }
}
