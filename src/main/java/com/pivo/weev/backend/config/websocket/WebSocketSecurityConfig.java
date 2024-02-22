package com.pivo.weev.backend.config.websocket;

import static com.pivo.weev.backend.rest.utils.Constants.Authorities.WRITE;
import static com.pivo.weev.backend.websocket.utils.Constants.ApplicationDestinations.APPLICATION_DESTINATION;
import static com.pivo.weev.backend.websocket.utils.Constants.BrokerDestinations.TOPIC_BROKER_DESTINATION;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.messaging.simp.SimpMessageType.DISCONNECT;
import static org.springframework.messaging.simp.SimpMessageType.MESSAGE;
import static org.springframework.messaging.simp.SimpMessageType.SUBSCRIBE;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@Order(HIGHEST_PRECEDENCE + 99)
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.nullDestMatcher().authenticated()
                .simpTypeMatchers(DISCONNECT).permitAll()
                .simpTypeMatchers(MESSAGE, SUBSCRIBE).authenticated()
                .simpSubscribeDestMatchers(TOPIC_BROKER_DESTINATION + ".**", TOPIC_BROKER_DESTINATION + "/**").hasAnyAuthority(WRITE)
                .simpDestMatchers(APPLICATION_DESTINATION + ".**", APPLICATION_DESTINATION + "/**").hasAnyAuthority(WRITE)
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
