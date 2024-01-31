package com.pivo.weev.backend.rest.utils;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getNullableUserId;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.meet.Meet;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MeetViewUtils {

    public static boolean hasPrivateData(Meet source) {
        if (!source.hasRestrictions()) {
            return false;
        }
        Long viewerId = getNullableUserId();
        if (!source.getRestrictions().isPublic()) {
            boolean unauthorized = isNull(viewerId);
            boolean isMemberOrCreator = !unauthorized && (Objects.equals(source.getCreator().getId(), viewerId) || source.hasMember(viewerId));
            return unauthorized || !isMemberOrCreator;
        }
        return false;
    }
}
