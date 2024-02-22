package com.pivo.weev.backend.domain.utils;

import static com.pivo.weev.backend.utils.ArrayUtils.mapToList;
import static org.apache.commons.lang3.StringUtils.SPACE;

import com.pivo.weev.backend.rest.utils.Constants.Claims;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@UtilityClass
public final class JwtUtils {

    public static String getDeviceId(Jwt jwt) {
        return jwt.getClaim(Claims.DEVICE_ID);
    }

    public static Long getUserId(Jwt jwt) {
        return jwt.getClaim(Claims.USER_ID);
    }

    public static String getNickname(Jwt jwt) {
        return jwt.getClaim(Claims.NICKNAME);
    }

    public static String getSerial(Jwt jwt) {
        return jwt.getClaim(Claims.SERIAL);
    }

    public static String getScope(Jwt jwt) {
        return jwt.getClaim(Claims.SCOPE);
    }

    public static List<SimpleGrantedAuthority> getGrantedAuthorities(Jwt jwt) {
        String scope = getScope(jwt);
        String [] authorities = StringUtils.split(scope, SPACE);
        return mapToList(authorities, SimpleGrantedAuthority::new);
    }
}
