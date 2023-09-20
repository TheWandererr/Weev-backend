package com.pivo.weev.backend.rest.config;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.pivo.weev.backend.rest.utils.Constants.Api.LOGIN_URL;
import static com.pivo.weev.backend.rest.utils.Constants.Authorities.WRITE;
import static com.pivo.weev.backend.rest.utils.Constants.RequestParameters.PASSWORD;
import static com.pivo.weev.backend.rest.utils.Constants.RequestParameters.USERNAME;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.NEVER;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey.Builder;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.OAuthTokenDetailsRepositoryWrapper;
import com.pivo.weev.backend.domain.service.auth.OAuthTokenService;
import com.pivo.weev.backend.rest.error.ErrorFactory;
import com.pivo.weev.backend.rest.filter.JWTVerifierFilter;
import com.pivo.weev.backend.rest.handler.AccessDeniedHandler;
import com.pivo.weev.backend.rest.handler.AuthenticationFailureHandler;
import com.pivo.weev.backend.rest.handler.AuthenticationSuccessHandler;
import com.pivo.weev.backend.rest.handler.UnauthorizedHandler;
import com.pivo.weev.backend.rest.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.service.AuthService;
import com.pivo.weev.backend.rest.service.LoginDetailsService;
import com.pivo.weev.backend.rest.service.security.JWTAuthenticityVerifier;
import com.pivo.weev.backend.rest.service.security.JWTClaimsVerifier;
import com.pivo.weev.backend.rest.service.security.RSAKeyService;
import com.pivo.weev.backend.rest.utils.LocaleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WeevBackendWebConfig implements WebMvcConfigurer {

    private final LoginDetailsService loginDetailsService;
    private final AuthService authService;
    private final RSAKeyService rsaKeyService;
    private final OAuthTokenService oauthTokenService;
    private final ErrorFactory errorFactory;

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public LocaleResolver serviceLocaleResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(LocaleUtils.getDefaultLocale());
        return sessionLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentTypeStrategy(new FixedContentNegotiationStrategy(APPLICATION_JSON));
        configurer.ignoreAcceptHeader(true);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ObjectMapper restResponseMapper,
                                           ApplicationLoggingHelper applicationLoggingHelper,
                                           JWTAuthenticityVerifier jwtAuthenticityVerifier)
            throws Exception {
        http.formLogin(customizer ->
                               customizer.loginProcessingUrl(LOGIN_URL)
                                         .usernameParameter(USERNAME)
                                         .passwordParameter(PASSWORD)
                                         .successHandler(
                                                 new AuthenticationSuccessHandler(
                                                         restResponseMapper,
                                                         applicationLoggingHelper,
                                                         authService,
                                                         oauthTokenService)
                                         )
                                         .failureHandler(
                                                 new AuthenticationFailureHandler(restResponseMapper, applicationLoggingHelper, errorFactory))
                                         .permitAll()
        );

        http.authorizeHttpRequests(customizer ->
                                           customizer.requestMatchers(GET).permitAll()
                                                     .requestMatchers(POST).hasAnyAuthority(WRITE)
                                                     .requestMatchers(PUT).hasAnyAuthority(WRITE)
                                                     .requestMatchers(DELETE).hasAnyAuthority(WRITE)
        );

        http.sessionManagement(customizer -> customizer.sessionCreationPolicy(NEVER));

        http.exceptionHandling(customizer ->
                                       customizer.accessDeniedHandler(new AccessDeniedHandler(restResponseMapper, applicationLoggingHelper, errorFactory))
                                                 .authenticationEntryPoint(new UnauthorizedHandler(restResponseMapper, applicationLoggingHelper, errorFactory))
        );

        http.oauth2ResourceServer(customizer -> customizer.jwt(withDefaults()));

        http.addFilterBefore(
                new JWTVerifierFilter(errorFactory, restResponseMapper, applicationLoggingHelper, jwtAuthenticityVerifier),
                UsernamePasswordAuthenticationFilter.class
        );

        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(loginDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

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

    @Bean
    @Primary
    public ObjectMapper restResponseMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public ApplicationLoggingHelper applicationLoggingHelper(ObjectMapper restResponseMapper) {
        return new ApplicationLoggingHelper(restResponseMapper);
    }
}
