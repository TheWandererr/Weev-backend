package com.pivo.weev.backend.domain.persistance.jpa.utils;

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

        public static final String EVENT_CATEGORY = "category";
        public static final String EVENT_SUBCATEGORY = "subcategory";
        public static final String EVENT_HEADER = "header";
        public static final String EVENT_STATUS = "status";
        public static final String EVENT_LOCATION = "location";
        public static final String EVENT_RELEVANCE = "relevance";
        public static final String EVENT_UTC_START_DATE_TIME = "utcStartDateTime";
        public static final String EVENT_UTC_END_DATE_TIME = "utcEndDateTime";

        public static final String EVENT_CATEGORY_NAME = "name";
        public static final String EVENT_SUBCATEGORY_NAME = "name";

        public static final String EVENT_LOCATION_POINT = "point";
    }

    @UtilityClass
    public static final class Paths {

        public static final String PATH_SPLITTER_LITERAL = ".";
        public static final String PATH_SPLITTER = Pattern.quote(PATH_SPLITTER_LITERAL);

        public static final String USER_NICKNAME = Columns.USER_NICKNAME;
        public static final String USER_EMAIL = Columns.USER_EMAIL;
        public static final String USER_PHONE_NUMBER = Columns.USER_PHONE_NUMBER;

        public static final String EVENT_UTC_START_DATE_TIME = Columns.EVENT_UTC_START_DATE_TIME;
        public static final String EVENT_UTC_END_DATE_TIME = Columns.EVENT_UTC_END_DATE_TIME;
        public static final String EVENT_STATUS = Columns.EVENT_STATUS;
        public static final String EVENT_HEADER = Columns.EVENT_HEADER;
        public static final String EVENT_CATEGORY = Columns.EVENT_CATEGORY;
        public static final String EVENT_SUBCATEGORY = Columns.EVENT_SUBCATEGORY;
        public static final String EVENT_LOCATION = Columns.EVENT_LOCATION + PATH_SPLITTER_LITERAL + Columns.EVENT_LOCATION_POINT;
    }

    @UtilityClass
    public static final class CriteriaFunctions {

        public static final String DISTANCE_3D = "st_dwithin";
        public static final String DATE_PART = "date_part";
    }

    @UtilityClass
    public static final class CriteriaLiterals {

        public static final String EPOCH = "epoch";
    }
}
