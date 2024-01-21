package com.pivo.weev.backend.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey.Builder;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.OAuthTokenDetailsRepositoryWrapper;
import com.pivo.weev.backend.rest.service.security.JWTAuthenticityVerifier;
import com.pivo.weev.backend.rest.service.security.JWTClaimsVerifier;
import com.pivo.weev.backend.rest.service.security.RSAKeyService;
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

    private final RSAKeyService rsaKeyService;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeyService.getPublicKey())
                               .jwtProcessorCustomizer(customizer -> customizer.setJWTClaimsSetVerifier(new JWTClaimsVerifier()))
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
    public JWTAuthenticityVerifier jwtAuthenticityVerifier(OAuthTokenDetailsRepositoryWrapper oAuthTokenDetailsRepository, JwtDecoder jwtDecoder) {
        return new JWTAuthenticityVerifier(oAuthTokenDetailsRepository, jwtDecoder);
    }
}
