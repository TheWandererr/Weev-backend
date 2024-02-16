package com.pivo.weev.backend.config.websocket;

import static com.pivo.weev.backend.rest.utils.Constants.Authorities.WRITE;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.APPLICATION_DESTINATION;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.TOPIC_DESTINATION;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.USER_DESTINATION;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.messaging.simp.SimpMessageType.DISCONNECT;
import static org.springframework.messaging.simp.SimpMessageType.MESSAGE;
import static org.springframework.messaging.simp.SimpMessageType.SUBSCRIBE;

import com.pivo.weev.backend.websocket.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@Order(HIGHEST_PRECEDENCE + 99)
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers(USER_DESTINATION + ".errors").permitAll()
                .simpDestMatchers(APPLICATION_DESTINATION + ".**").hasAnyAuthority(WRITE)
                .simpSubscribeDestMatchers(USER_DESTINATION + ".**", TOPIC_DESTINATION + ".**").hasRole(WRITE)
                .simpTypeMatchers(MESSAGE, SUBSCRIBE).authenticated()
                .simpTypeMatchers(DISCONNECT).permitAll()
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void customizeClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authInterceptor);
    }
}
