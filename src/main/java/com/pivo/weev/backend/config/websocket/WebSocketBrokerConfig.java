package com.pivo.weev.backend.config.websocket;

import static com.pivo.weev.backend.utils.Constants.Symbols.DOT;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.APPLICATION_DESTINATION;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.TOPIC_DESTINATION;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.USER_DESTINATION;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.STOMP_ENDPOINT;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.config.rabbit.properties.RabbitProperties;
import com.pivo.weev.backend.websocket.interceptor.AuthInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Order(HIGHEST_PRECEDENCE + 98)
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    private final ObjectMapper objectMapper;
    private final RabbitProperties rabbitProperties;
    private final AuthInterceptor authInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(STOMP_ENDPOINT);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher(DOT))
                .setApplicationDestinationPrefixes(APPLICATION_DESTINATION)
                .setUserDestinationPrefix(USER_DESTINATION)
                .enableStompBrokerRelay(TOPIC_DESTINATION)
                .setRelayHost(rabbitProperties.getHost())
                .setRelayPort(rabbitProperties.getPort())
                .setClientLogin(rabbitProperties.getUsername())
                .setClientPasscode(rabbitProperties.getPassword());
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        messageConverters.add(converter);
        return true;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authInterceptor);
    }
}
