package com.pivo.weev.backend.config.messaging.rabbit.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitProperties {

    private String host;
    private int port;
    private String username;
    private String password;
}
