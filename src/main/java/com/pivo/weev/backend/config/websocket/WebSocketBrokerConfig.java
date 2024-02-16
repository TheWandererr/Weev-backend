package com.pivo.weev.backend.config.websocket;

import static com.pivo.weev.backend.utils.Constants.Symbols.DOT;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.APPLICATION_DESTINATION;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.TOPIC_DESTINATION;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.Mappings.USER_DESTINATION;
import static com.pivo.weev.backend.utils.Constants.WebSocketParams.STOMP_ENDPOINT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.config.rabbit.properties.RabbitProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    private final ObjectMapper objectMapper;
    private final RabbitProperties rabbitProperties;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(STOMP_ENDPOINT);
        registry.addEndpoint(STOMP_ENDPOINT)
                .withSockJS();
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
}
