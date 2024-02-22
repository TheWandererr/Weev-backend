package com.pivo.weev.backend.config.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey.Builder;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.AuthTokensDetailsRepository;
import com.pivo.weev.backend.domain.service.jwt.JwtAuthenticityVerifier;
import com.pivo.weev.backend.domain.service.jwt.JwtClaimsVerifier;
import com.pivo.weev.backend.domain.service.jwt.RsaKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final RsaKeyService rsaKeyService;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeyService.getPublicKey())
                               .jwtProcessorCustomizer(customizer -> customizer.setJWTClaimsSetVerifier(new JwtClaimsVerifier()))
                               .build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new Builder(rsaKeyService.getPublicKey())
                .privateKey(rsaKeyService.getPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtAuthenticityVerifier jwtAuthenticityVerifier(AuthTokensDetailsRepository authTokenDetailsRepository) {
        return new JwtAuthenticityVerifier(authTokenDetailsRepository);
    }
}
