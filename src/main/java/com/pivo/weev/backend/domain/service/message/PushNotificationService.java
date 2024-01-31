package com.pivo.weev.backend.domain.service.message;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.integration.firebase.MessagingService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final MessagingService messagingService;

    public void notifyAll(MeetJpa meet, List<DeviceJpa> devices, String title, Map<String, Object> details) {

    }
}
