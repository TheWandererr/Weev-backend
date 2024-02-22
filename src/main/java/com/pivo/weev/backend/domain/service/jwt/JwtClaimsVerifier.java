package com.pivo.weev.backend.domain.service.jwt;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.pivo.weev.backend.rest.utils.Constants.Api;
import com.pivo.weev.backend.rest.utils.Constants.Claims;
import java.util.Set;

public class JwtClaimsVerifier extends DefaultJWTClaimsVerifier<SecurityContext> {

    private static final Set<String> REQUIRED_CLAIMS = Set.of(Claims.SCOPE, Claims.SERIAL, Claims.USER_ID, Claims.DEVICE_ID);

    public JwtClaimsVerifier() {
        super(Api.PREFIX, null, REQUIRED_CLAIMS);
    }

}
