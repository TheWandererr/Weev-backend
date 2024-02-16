package com.pivo.weev.backend.websocket.interceptor;

import static com.pivo.weev.backend.domain.utils.JwtUtils.getGrantedAuthorities;
import static com.pivo.weev.backend.rest.utils.Constants.Headers.DEVICE_ID;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

import com.pivo.weev.backend.rest.model.jwt.JwtVerificationResult;
import com.pivo.weev.backend.rest.service.jwt.JwtAuthenticityVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements ChannelInterceptor {

    private final AuthenticationProvider authenticationProvider;

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticityVerifier jwtAuthenticityVerifier;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (isNull(accessor)) {
            return message;
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            boolean authenticated = handleAuthentication(accessor);
            if (!authenticated) {
                return message;
            }
        }
        return message;
    }

    private boolean handleAuthentication(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(AUTHORIZATION);
        if (isBlank(authorization)) {
            return false;
        }
        String token = authorization.substring(BEARER.getValue().length());
        Jwt jwt = jwtDecoder.decode(token);
        String deviceId = accessor.getFirstNativeHeader(DEVICE_ID);
        JwtVerificationResult verificationResult = jwtAuthenticityVerifier.verify(jwt, deviceId);
        if (!verificationResult.isSuccessful()) {
            return false;
        }

        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, getGrantedAuthorities(jwt));
        Authentication authentication = authenticationProvider.authenticate(authenticationToken);
        accessor.setUser(authentication);
        return true;
    }
}
