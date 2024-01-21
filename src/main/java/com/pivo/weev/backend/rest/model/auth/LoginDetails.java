package com.pivo.weev.backend.rest.model.auth;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.utils.CollectionUtils;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = {"userId", "deviceId", "username", "password"})
public class LoginDetails implements UserDetails {

    private final Long userId;
    private final String deviceId;
    private final String issuer;
    private final String serial;
    private final String username;
    private final String password;
    private final boolean active;
    private final List<SimpleGrantedAuthority> authenticationAuthorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authenticationAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive();
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    public static LoginDetails from(UserJpa userJpa, String username, String deviceId, String issuer) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = CollectionUtils.mapToList(
                userJpa.getRole().getAuthorities(),
                authorityJpa -> new SimpleGrantedAuthority(authorityJpa.getValue())
        );
        return new LoginDetails(
                userJpa.getId(),
                deviceId,
                issuer,
                UUID.randomUUID().toString(),
                username,
                userJpa.getPassword(),
                isTrue(userJpa.getActive()),
                simpleGrantedAuthorities
        );
    }
}
