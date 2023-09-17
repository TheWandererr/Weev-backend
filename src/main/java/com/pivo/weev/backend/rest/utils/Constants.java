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


        public static final String PERMISSIONS = "not.enough.permissions";
        public static final String UNAUTHORIZED = "unauthorized";
        public static final String FORBIDDEN = "forbidden";
        public static final String VALIDATION_FAILED = "validation.failed";
        public static final String NOT_FOUND = "not.found";
        public static final String MUST_BE_NOT_NULL = "must.be.not.null";
        public static final String MUST_BE_NOT_BLANK = "must.be.not.blank";
        public static final String MUST_BE_NULL_OR_NOT_BLANK = "must.be.null.or.not.blank";
        public static final String INVALID_EVENT_HEADER = "incorrect.header.length";
        public static final String INVALID_EVENT_CATEGORY = "unsupported.category";
        public static final String INVALID_EVENT_SUBCATEGORY = "unsupported.subcategory";
        public static final String INVALID_EVENT_MEMBERS_AMOUNT = "incorrect.members.limit";
        public static final String LENGTH_OUT_OF_BOUND = "length.out.of.bound";
        public static final String INCORRECT_FEE_AMOUNT = "incorrect.fee.amount";
        public static final String UNSUPPORTED_FILE = "unsupported.file";
        public static final String FLOW_INTERRUPTED_ERROR = "flow.interrupted.error";
        public static final String INVALID_DECLINATION_REASON = "unsupported.declination.reason";
    }

    @UtilityClass
    public static final class ErrorMessageCodes {

        public static final String INVALID_TOKEN = "invalid.token";
        public static final String MISSING_COOKIE = "missing.cookie";
        public static final String AUTHENTICATION_FAILED = "authentication.failed";
        public static final String NOT_ENOUGH_PERMISSIONS = "not.enough.permissions";
        public static final String FLOW_INTERRUPTED = "flow.interrupted";
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

    @UtilityClass
    public static final class FileMediaTypes {

        public static final String IMAGE = "image";
    }

    @UtilityClass
    public static final class DateTimePatterns {

        public static final String DEFAULT_LOCAL_DATE_TIME_PATTERN = "yyyy-dd-MM HH:mm";
    }
}
