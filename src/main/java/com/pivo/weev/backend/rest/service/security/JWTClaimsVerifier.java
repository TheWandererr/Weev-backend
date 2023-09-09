package com.pivo.weev.backend.rest.service.security;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.pivo.weev.backend.rest.utils.Constants.Claims;
import com.pivo.weev.backend.rest.utils.Constants.Api;
import java.util.Set;

public class JWTClaimsVerifier extends DefaultJWTClaimsVerifier<SecurityContext> {

  private static final Set<String> REQUIRED_CLAIMS = Set.of(Claims.SCOPE, Claims.SERIAL, Claims.USER_ID, Claims.DEVICE_ID);

  public JWTClaimsVerifier() {
    super(Api.PREFIX, null, REQUIRED_CLAIMS);
  }

}
