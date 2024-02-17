package com.pivo.weev.backend.websocket.utils;

import static com.pivo.weev.backend.websocket.utils.Constants.Headers.SIMP_DESTINATION;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@UtilityClass
public class StompUtils {

    public static String getNativeHeader(StompHeaderAccessor accessor, String name) {
        return accessor.getFirstNativeHeader(name);
    }

    public static String getAuthorization(StompHeaderAccessor accessor) {
        return getNativeHeader(accessor, AUTHORIZATION.toLowerCase());
    }

    public static String getAuthorizationToken(StompHeaderAccessor accessor) {
        String authorization = getAuthorization(accessor);
        return isNotBlank(authorization) && authorization.startsWith(BEARER.getValue())
                ? authorization.substring(BEARER.getValue().length())
                : authorization;
    }

    public static String getDestination(SessionSubscribeEvent event) {
        return Optional.of(event)
                .map(AbstractSubProtocolEvent::getMessage)
                .map(Message::getHeaders)
                .map(headers -> headers.get(SIMP_DESTINATION))
                .map(String.class::cast)
                .orElse(null);
    }
}
