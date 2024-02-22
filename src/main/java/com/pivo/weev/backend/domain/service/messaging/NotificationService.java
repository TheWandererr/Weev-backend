package com.pivo.weev.backend.domain.service.messaging;

import static com.pivo.weev.backend.utils.CollectionUtils.mapToSet;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.NotificationMapper;
import com.pivo.weev.backend.domain.model.user.Notification;
import com.pivo.weev.backend.domain.persistance.jpa.NotificationFactory;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.DeclinationReasonJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetNotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.NotificationRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationFactory notificationFactory;

    public void notify(MeetJpa target, UserJpa recipient, String topic) {
        notify(target, recipient, topic, new HashMap<>());
    }

    public void notify(MeetJpa target, UserJpa recipient, String topic, Map<String, Object> details) {
        MeetNotificationJpa notification = notificationFactory.createMeetNotification(target, recipient, topic, details);
        notificationRepository.save(notification);
    }

    public void notify(MeetJpa target, UserJpa recipient, String topic, DeclinationReasonJpa reason) {
        NotificationJpa notification = notificationFactory.createMeetNotification(target, recipient, topic, reason);
        notificationRepository.save(notification);
    }

    public void notifyAll(MeetJpa target, Collection<UserJpa> recipients, String topic) {
        notifyAll(target, recipients, topic, null);
    }

    public void notifyAll(MeetJpa target, Collection<UserJpa> recipients, String topic, Map<String, Object> details) {
        Set<NotificationJpa> notifications = mapToSet(recipients, recipient -> notificationFactory.createMeetNotification(target, recipient, topic, details));
        notificationRepository.saveAll(notifications);
    }

    public Page<Notification> getNotifications(Long userId, Pageable pageable) {
        Page<NotificationJpa> jpaPage = notificationRepository.findAllByRecipientId(userId, pageable);
        List<Notification> content = getMapper(NotificationMapper.class).map(jpaPage.getContent());
        return new PageImpl<>(content, jpaPage.getPageable(), jpaPage.getTotalElements());
    }
}
