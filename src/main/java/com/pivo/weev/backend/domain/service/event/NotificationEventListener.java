package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.utils.CollectionUtils.flatMapToList;
import static com.pivo.weev.backend.utils.StreamUtils.select;

import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent.PushNotificationModel;
import com.pivo.weev.backend.domain.model.user.Device;
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
        PushNotificationModel model = event.getSource();
        List<Device> devices = flatMapToList(model.getRecipients(), recipient -> select(recipient.getDevices(), Device::hasPushNotificationToken));
        pushNotificationService.notifyAll(
                model.getMeet(),
                devices,
                model.getTopic(),
                model.getDetails()
        );
    }
}
