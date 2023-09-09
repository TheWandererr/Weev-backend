package com.pivo.weev.backend.rest.utils;

import java.util.Set;
import java.util.TimeZone;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  @UtilityClass
  public static final class Configs {

    public static final TimeZone APPLICATION_TIMEZONE = TimeZone.getTimeZone("UTC");
  }

  @UtilityClass
  public static final class Languages {

    public static final String DEFAULT_LANGUAGE = "en";
    public static final String RU = "ru";
    public static final Set<String> ACCEPTED_LANGUAGES = Set.of(RU, DEFAULT_LANGUAGE);
  }

  @UtilityClass
  public static final class Api {

    public static final String PREFIX = "/weev/api";
    public static final String LOGIN_URL = "/weev/auth/login";
    public static final String LOGIN_URI = "/auth/login";
    public static final String LOGOUT_URL = "/weev/auth/logout";
    public static final String REFRESH_URI = "/auth/refresh";
  }

  @UtilityClass
  public static final class RequestParameters {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
  }

  @UtilityClass
  public static final class Authorities {

    private static final String PREFIX = "SCOPE_";
    public static final String READ = PREFIX + "read";
    public static final String WRITE = PREFIX + "write";
  }

  @UtilityClass
  public static final class Cookies {

    public static final String DEVICE_ID = "deviceId";
  }

  @UtilityClass
  public static final class ErrorCodes {

    public static final String AUTHENTICATION_FAILED_ERROR = "authentication.failed";
    public static final String PERMISSIONS_ERROR = "not.enough.permissions";
    public static final String AUTHORIZATION_REQUIRED_ERROR = "unauthorized";
    public static final String FORBIDDEN_ERROR = "forbidden";
  }

  @UtilityClass
  public static final class ErrorMessages {

    public static final String PERMISSION_DENIED = "permission denied";
    public static final String TOKEN_REVOKED = "revoked";
    public static final String MISSING_COOKIE = "required cookie is missing";
    public static final String AUTHENTICATION_CREDENTIALS = "bad credentials";
  }

  @UtilityClass
  public static final class ResponseDetails {

    public static final String REASON = "reason";
  }

  @UtilityClass
  public static final class Claims {

    public static final String DEVICE_ID = "deviceId";
    public static final String USER_ID = "userId";
    public static final String SCOPE = "scope";
    public static final String MODE = "mode";
    public static final String SERIAL = "serial";
  }

  @UtilityClass
  public static final class JWTModes {

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";
  }
}
