package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.utils.CollectionUtils.flatMapToList;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent.PushNotificationModel;
import com.pivo.weev.backend.domain.service.message.PushNotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final PushNotificationService pushNotificationService;

    @Async(value = "commonExecutor")
    @EventListener
    public void onPushNotificationEventPublishing(PushNotificationEvent event) {
        PushNotificationModel pushNotificationMessage = event.getSource();
        List<DeviceJpa> devices = flatMapToList(pushNotificationMessage.recipients(), recipient -> recipient.getDevices().stream());
        pushNotificationService.notifyAll(
                pushNotificationMessage.meet(),
                devices,
                pushNotificationMessage.title(),
                pushNotificationMessage.details()
        );
    }
}
