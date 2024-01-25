package com.pivo.weev.backend.domain.service.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter
public class JwtHolder {

    private Jwt token;
}
