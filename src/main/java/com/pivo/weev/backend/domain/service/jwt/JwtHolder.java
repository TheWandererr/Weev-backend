package com.pivo.weev.backend.domain.service.jwt;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "thread", proxyMode = TARGET_CLASS)
@Getter
@Setter
public class JwtHolder {

    private Jwt token;

    public void clear() {
        setToken(null);
    }
}
