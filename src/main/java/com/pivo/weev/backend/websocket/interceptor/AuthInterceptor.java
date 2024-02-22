package com.pivo.weev.backend.websocket.interceptor;

import static com.pivo.weev.backend.domain.utils.JwtUtils.getGrantedAuthorities;
import static com.pivo.weev.backend.rest.utils.Constants.Headers.DEVICE_ID;
import static com.pivo.weev.backend.websocket.utils.StompUtils.getAuthorizationToken;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import com.pivo.weev.backend.domain.model.jwt.JwtVerificationResult;
import com.pivo.weev.backend.domain.service.jwt.JwtAuthenticityVerifier;
import com.pivo.weev.backend.domain.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(HIGHEST_PRECEDENCE + 69)
public class AuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticityVerifier jwtAuthenticityVerifier;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (isNull(accessor)) {
            return message;
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            tryAuthenticate(accessor);
        }
        return message;
    }

    private void tryAuthenticate(StompHeaderAccessor accessor) {
        String token = getAuthorizationToken(accessor);
        if (isBlank(token)) {
            return;
        }
        Jwt jwt = jwtDecoder.decode(token);
        String deviceId = accessor.getFirstNativeHeader(DEVICE_ID);
        JwtVerificationResult verificationResult = jwtAuthenticityVerifier.verify(jwt, deviceId);
        if (!verificationResult.isSuccessful()) {
            return;
        }

        String nickname = JwtUtils.getNickname(jwt);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(nickname, null, getGrantedAuthorities(jwt));
        accessor.setUser(authenticationToken);
    }
}
