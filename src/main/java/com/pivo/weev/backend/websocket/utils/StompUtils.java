package com.pivo.weev.backend.websocket.utils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import lombok.experimental.UtilityClass;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@UtilityClass
public class StompUtils {

    public static String getNativeHeader(StompHeaderAccessor accessor, String name) {
        return accessor.getFirstNativeHeader(name);
    }

    public static String getAuthorization(StompHeaderAccessor accessor) {
        return getNativeHeader(accessor, AUTHORIZATION.toLowerCase());
    }

    public static String getToken(StompHeaderAccessor accessor) {
        String authorization = getAuthorization(accessor);
        return isNotBlank(authorization) && authorization.startsWith(BEARER.getValue())
                ? authorization.substring(BEARER.getValue().length())
                : authorization;
    }


}
