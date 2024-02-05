package com.pivo.weev.backend.domain.model.auth;

import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.utils.Randomizer.uuid;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record LoginDetails(UserJpa user,
                           String deviceId,
                           String issuer,
                           String serial,
                           String username,
                           String password,
                           boolean active,
                           List<SimpleGrantedAuthority> authenticationAuthorities) implements UserDetails {

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
        return active();
    }

    @Override
    public boolean isAccountNonLocked() {
        return active();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active();
    }

    @Override
    public boolean isEnabled() {
        return active() && !user.isDeleted();
    }

    public Long getUserId() {
        return user.getId();
    }

    public static LoginDetails from(UserJpa user, String username, String deviceId, String issuer) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = mapToList(
                user.getRole().getAuthorities(),
                authorityJpa -> new SimpleGrantedAuthority(authorityJpa.getValue())
        );
        return new LoginDetails(
                user,
                deviceId,
                issuer,
                uuid(),
                username,
                user.getPassword(),
                isTrue(user.getActive()),
                simpleGrantedAuthorities
        );
    }
}
