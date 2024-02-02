package com.pivo.weev.backend.domain.utils;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.AUTHENTICATION_PRINCIPAL_NOT_FOUND_ERROR;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import com.pivo.weev.backend.domain.model.auth.LoginDetails;
import com.pivo.weev.backend.utils.CollectionUtils;
import java.util.Collection;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@UtilityClass
public class AuthUtils {

    public static LoginDetails getLoginDetails() {
        return getOptionalLoginDetails()
                .orElseThrow(() -> new AuthorizationServiceException(AUTHENTICATION_PRINCIPAL_NOT_FOUND_ERROR));
    }

    public static LoginDetails getLoginDetails(Authentication authentication) {
        return getOptionalLoginDetails(authentication)
                .orElseThrow(() -> new AuthorizationServiceException(AUTHENTICATION_PRINCIPAL_NOT_FOUND_ERROR));
    }

    private static Optional<LoginDetails> getOptionalLoginDetails() {
        return getOptionalLoginDetails(getAuthentication());
    }

    private static Optional<LoginDetails> getOptionalLoginDetails(Authentication authentication) {
        return ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .filter(LoginDetails.class::isInstance)
                .map(LoginDetails.class::cast);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Collection<SimpleGrantedAuthority> getGrantedAuthorities() {
        return ofNullable(getAuthentication())
                .map(Authentication::getAuthorities)
                .map(authorities -> CollectionUtils.selectToList(authorities, SimpleGrantedAuthority.class::isInstance,
                                                                 SimpleGrantedAuthority.class::cast))
                .orElse(emptyList());
    }

    private static Jwt getAuthenticationDetails() {
        return getOptionalAuthenticationDetails()
                .orElseThrow(() -> new AuthorizationServiceException(AUTHENTICATION_PRINCIPAL_NOT_FOUND_ERROR));
    }

    private static Optional<Jwt> getOptionalAuthenticationDetails() {
        return ofNullable(getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(Jwt.class::isInstance)
                .map(Jwt.class::cast);
    }

    public static Long getUserId() {
        return JwtUtils.getUserId(getAuthenticationDetails());
    }

    public static Long getNullableUserId() {
        return getOptionalAuthenticationDetails()
                .map(JwtUtils::getUserId)
                .orElse(null);
    }
}
