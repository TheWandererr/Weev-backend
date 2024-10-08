package com.pivo.weev.backend.config.security;

import static com.pivo.weev.backend.rest.utils.Constants.Api.EMAILS_URI;
import static com.pivo.weev.backend.rest.utils.Constants.Api.LOGIN_URL;
import static com.pivo.weev.backend.rest.utils.Constants.Api.MEETS_SEARCH_MAP_URI;
import static com.pivo.weev.backend.rest.utils.Constants.Api.MEETS_SEARCH_URI;
import static com.pivo.weev.backend.rest.utils.Constants.Api.PASSWORD_RESET_URI;
import static com.pivo.weev.backend.rest.utils.Constants.Api.REGISTRATION_URI;
import static com.pivo.weev.backend.rest.utils.Constants.Api.VERIFICATION_COMPLETION_URI;
import static com.pivo.weev.backend.rest.utils.Constants.Authorities.WRITE;
import static com.pivo.weev.backend.rest.utils.Constants.RequestParameters.PASSWORD;
import static com.pivo.weev.backend.rest.utils.Constants.RequestParameters.USERNAME;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.NEVER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.domain.service.auth.AuthTokensDetailsService;
import com.pivo.weev.backend.domain.service.auth.AuthTokensService;
import com.pivo.weev.backend.domain.service.auth.LoginDetailsService;
import com.pivo.weev.backend.domain.service.jwt.JwtAuthenticityVerifier;
import com.pivo.weev.backend.domain.service.jwt.JwtHolder;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import com.pivo.weev.backend.rest.error.AlertRestFactory;
import com.pivo.weev.backend.rest.error.NotificationRestFactory;
import com.pivo.weev.backend.rest.error.PopupRestFactory;
import com.pivo.weev.backend.rest.filter.JwtAuthenticityVerifierFilter;
import com.pivo.weev.backend.rest.filter.JwtDecoderFilter;
import com.pivo.weev.backend.rest.handler.AccessDeniedHandler;
import com.pivo.weev.backend.rest.handler.AuthenticationFailureProcessor;
import com.pivo.weev.backend.rest.handler.AuthenticationSuccessHandler;
import com.pivo.weev.backend.rest.handler.UnauthorizedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final LoginDetailsService loginDetailsService;
    private final AuthTokensService authTokensService;
    private final AuthTokensDetailsService authTokensDetailsService;
    private final PopupRestFactory popupRestFactory;
    private final AlertRestFactory alertRestFactory;
    private final NotificationRestFactory notificationRestFactory;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ObjectMapper mapper,
                                           JwtHolder jwtHolder,
                                           JwtDecoder jwtDecoder,
                                           ApplicationLoggingHelper applicationLoggingHelper,
                                           JwtAuthenticityVerifier jwtAuthenticityVerifier)
            throws Exception {
        http.authorizeHttpRequests(customizer ->
                                           customizer.requestMatchers(GET).permitAll()
                                                     .requestMatchers(POST,
                                                                      MEETS_SEARCH_URI,
                                                                      MEETS_SEARCH_MAP_URI,
                                                                      PASSWORD_RESET_URI + "/**",
                                                                      REGISTRATION_URI + "/**",
                                                                      EMAILS_URI + "/**").permitAll()
                                                     .requestMatchers(POST).hasAnyAuthority(WRITE)
                                                     .requestMatchers(PUT,
                                                                      PASSWORD_RESET_URI + "/**",
                                                                      REGISTRATION_URI + "/**",
                                                                      EMAILS_URI + "/**",
                                                                      VERIFICATION_COMPLETION_URI).permitAll()
                                                     .requestMatchers(PUT).hasAnyAuthority(WRITE)
                                                     .requestMatchers(DELETE).hasAnyAuthority(WRITE)
        );

        http.formLogin(customizer ->
                               customizer.loginProcessingUrl(LOGIN_URL)
                                         .usernameParameter(USERNAME)
                                         .passwordParameter(PASSWORD)
                                         .successHandler(
                                                 new AuthenticationSuccessHandler(
                                                         mapper,
                                                         applicationLoggingHelper,
                                                         authTokensService,
                                                         authTokensDetailsService)
                                         )
                                         .failureHandler(
                                                 new AuthenticationFailureProcessor(mapper, applicationLoggingHelper, notificationRestFactory))
                                         .permitAll()
        );

        http.sessionManagement(customizer -> customizer.sessionCreationPolicy(NEVER));

        http.exceptionHandling(customizer ->
                                       customizer.accessDeniedHandler(new AccessDeniedHandler(mapper, applicationLoggingHelper, alertRestFactory))
                                                 .authenticationEntryPoint(new UnauthorizedHandler(mapper, applicationLoggingHelper, notificationRestFactory))
        );

        http.oauth2ResourceServer(customizer -> customizer.jwt(withDefaults()));

        http.addFilterBefore(
                new JwtAuthenticityVerifierFilter(notificationRestFactory, mapper, applicationLoggingHelper, jwtAuthenticityVerifier, jwtHolder),
                UsernamePasswordAuthenticationFilter.class
        );
        http.addFilterBefore(new JwtDecoderFilter(mapper, jwtHolder, jwtDecoder, popupRestFactory, applicationLoggingHelper),
                             JwtAuthenticityVerifierFilter.class
        );

        http.cors(withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
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
}
