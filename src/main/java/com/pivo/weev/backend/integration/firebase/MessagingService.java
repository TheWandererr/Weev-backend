package com.pivo.weev.backend.integration.firebase;

import com.pivo.weev.backend.integration.firebase.client.MessagingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final MessagingClient client;
}
