package com.pivo.weev.backend.domain.persistance.utils;

import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    @UtilityClass
    public static final class Errors {

        public static final String REQUESTED_RESOURCE_NOT_FOUND = "requested.%s.not.found";
        public static final String REQUESTED_RESOURCE_ALREADY_EXISTS = "requested.%s.already.exists";
    }

    @UtilityClass
    public static final class Columns {

        public static final String ID = "id";
        public static final String CREATED_DATE = "createdDate";
        public static final String MODIFIED_DATE = "modifiedDate";

        public static final String USER_NICKNAME = "nickname";
        public static final String USER_NAME = "name";
        public static final String USER_EMAIL = "email";
        public static final String USER_PASSWORD = "password";
        public static final String USER_PHONE_NUMBER = "phoneNumber";

        public static final String TOKEN_DETAILS_USER_ID = "userId";
        public static final String TOKEN_DETAILS_DEVICE_ID = "deviceId";

        public static final String EVENT_HEADER = "header";
        public static final String EVENT_STATUS = "status";
        public static final String EVENT_UTC_START_DATE_TIME = "utcStartDateTime";
        public static final String EVENT_UTC_END_DATE_TIME = "utcEndDateTime";

        public static final String EVENT_CATEGORY_NAME = "name";
        public static final String EVENT_SUBCATEGORY_NAME = "name";
    }

    @UtilityClass
    public static final class Paths {

        private static final String PATH_SPLITTER_LITERAL = ".";
        public static final String PATH_SPLITTER = Pattern.quote(PATH_SPLITTER_LITERAL);
    }

    @UtilityClass
    public static final class CriteriaFunctions {

        public static final String DISTANCE_3D = "st_dwithin";
        public static final String DATE_PART = "date_part";
        public static final String ST_CONTAINS = "st_contains";
        public static final String ST_MAKE_ENVELOPE = "st_MakeEnvelope";
    }

    @UtilityClass
    public static final class CriteriaLiterals {

        public static final String EPOCH = "epoch";
    }

    @UtilityClass
    public static final class Configs {

        public static final String IMAGE_COMPRESSING_SCALE = "_image_compressing_scale";
        public static final String ACCESS_TOKEN_EXPIRES_AMOUNT = "_access_token_expires_amount";
        public static final String REFRESH_TOKEN_EXPIRES_AMOUNT = "_refresh_token_expires_amount";
        public static final String VERIFICATION_REQUEST_VALIDITY_PERIOD = "_verification_request_validity_period";
    }

    @UtilityClass
    public static final class Firestore {

        @UtilityClass
        public static final class Collections {

            public static final String CONFIGS = "configs";
        }

    }

    @UtilityClass
    public static final class UserRoles {

        public static final String USER = "USER";
    }
}
