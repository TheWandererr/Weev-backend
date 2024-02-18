package com.pivo.weev.backend.rest.controller;

import com.pivo.weev.backend.domain.service.messaging.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class NotificationsController {

    private final NotificationService notificationService;
}
