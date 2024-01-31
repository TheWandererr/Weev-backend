package com.pivo.weev.backend.domain.service.event.message;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.Map;
import java.util.Set;

public record PushNotificationMessage(MeetJpa meet, Set<UserJpa> recipients, String title, Map<String, Object> details) {

}
