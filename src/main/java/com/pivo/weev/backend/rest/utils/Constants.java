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

        public static final String PREFIX = "/api";
        public static final String LOGIN_URL = "/auth/login";
        public static final String MEETS_SEARCH_URI = "/api/meets/search";
        public static final String MEETS_SEARCH_MAP_URI = "/api/meets/map/search";
        public static final String REGISTRATION_URI = "/auth/registration";
        public static final String EMAILS_URI = "/auth/email";
        public static final String PASSWORD_RESET_URI = "/auth/password/reset";
        public static final String VERIFICATION_COMPLETION_URI = "/auth/verification/completion";
    }

    @UtilityClass
    public static final class RequestParameters {

        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String CODE = "code";
    }

    @UtilityClass
    public static final class Authorities {

        private static final String PREFIX = "SCOPE_";
        public static final String READ = PREFIX + "read";
        public static final String WRITE = PREFIX + "write";
    }

    @UtilityClass
    public static final class Headers {

        public static final String DEVICE_ID = "X-DeviceId";
    }

    @UtilityClass
    public static final class ResponseDetails {

        public static final String REASON = "reason";
        public static final String TITLE = ".title";
    }

    @UtilityClass
    public static final class Claims {

        public static final String DEVICE_ID = "deviceId";
        public static final String USER_ID = "userId";
        public static final String SCOPE = "scope";
        public static final String NICKNAME = "nickname";
        public static final String SERIAL = "serial";
    }

    @UtilityClass
    public static final class JwtModes {

        public static final String ACCESS = "access";
        public static final String REFRESH = "refresh";
    }

    @UtilityClass
    public static final class FileMediaTypes {

        public static final String IMAGE = "image";
    }

    @UtilityClass
    public static final class DateTimePatterns {

        public static final String YYYY_MM_DD_HH_MM_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    }

    @UtilityClass
    public static final class PageableParams {

        public static final Integer MEETS_PER_PAGE = 5;
        public static final Integer MEET_REQUESTS_PER_PAGE = 10;
        public static final Integer MEET_TEMPLATES_PER_PAGE = 10;
        public static final String MESSAGES_PER_PAGE = "20";
        public static final Integer NOTIFICATIONS_PER_PAGE = 20;
    }

    @UtilityClass
    public static final class MapParams {

        public static final int MINIMAL_ZOOM = 1;
        public static final int MAXIMUM_ZOOM = 30;
    }

    @UtilityClass
    public static final class PopupButtons {

        public static final String OK = "ok";
        public static final String CANCEL = "cancel";
        public static final String LOGIN = "login";

    }
}
