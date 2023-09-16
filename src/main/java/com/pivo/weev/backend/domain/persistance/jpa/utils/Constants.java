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

        public static final String EVENT_CATEGORY_NAME = "name";
        public static final String EVENT_SUBCATEGORY_NAME = "name";
    }

    @UtilityClass
    public static final class Paths {

        private static final String PATH_SPLITTER_LITERAL = ".";
        public static final String PATH_SPLITTER = Pattern.quote(PATH_SPLITTER_LITERAL);
        public static final String USER_NICKNAME = Columns.USER_NICKNAME;
        public static final String USER_EMAIL = Columns.USER_EMAIL;
        public static final String USER_PHONE_NUMBER = Columns.USER_PHONE_NUMBER;
    }
}
