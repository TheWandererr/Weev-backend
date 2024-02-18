package com.pivo.weev.backend.domain.service.event;

import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent.PushNotificationModel;
import com.pivo.weev.backend.domain.service.messaging.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushNotificationEventListener {

    private final PushNotificationService pushNotificationService;

    @Async(value = "commonExecutor")
    @EventListener
    public void onPushNotificationEventPublishing(PushNotificationEvent event) {
        PushNotificationModel model = event.getSource();
        pushNotificationService.sendPushNotifications(
                model.getMeet(),
                model.getRecipients(),
                model.getTopic(),
                model.getPayload()
        );
    }
}
