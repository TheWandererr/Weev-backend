package com.pivo.weev.backend.domain.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {

    @UtilityClass
    public static final class EncryptionPatterns {

        public static final String PRIVATE_KEY_MASK_SYMBOLS = "###";
    }

    @UtilityClass
    public static final class ValidatableFields {

        public static final String LOCAL_START_DATE_TIME = "localStartDateTime";
        public static final String LOCAL_END_DATE_TIME = "localEndDateTime";
        public static final String MEMBERS_LIMIT = "membersLimit";
    }

    @UtilityClass
    public static final class CompressingParams {

        public static final Double MAX_SCALING = 0.2;
    }

    @UtilityClass
    public static final class NotificationTitles {

        public static final String MEET_CONFIRMATION = "meet.confirmation";
        public static final String MEET_UPDATE_SUCCESSFUL = "meet.update.successful";
        public static final String MEET_UPDATE_FAILED = "meet.update.failed";
        public static final String MEET_DECLINATION = "meet.declination";
        public static final String MEET_CANCELLATION = "meet.cancellation";
        public static final String MEET_NEW_JOIN_REQUEST = "meet.new.join.request";
        public static final String MEET_JOIN_REQUEST_CONFIRMATION = "meet.join.request.confirmation";
    }

    @UtilityClass
    public static final class NotificationDetails {

        public static final String REQUESTER = "requester";
    }

    @UtilityClass
    public static final class MapParams {

        public static final int MINIMAL_ZOOM = 4;
        public static final int MAXIMUM_ZOOM = 22;
    }

    @UtilityClass
    public static final class Messaging {

        @UtilityClass
        public static final class EmailMessages {

            public static final String EMAIL_VERIFICATION_REQUEST_SUBJECT = "email.verification.request.subject";
        }

        @UtilityClass
        public static final class Templates {

            public static final String EMAIL_VERIFICATION_REQUEST = "email-verification.ftl";
        }
    }
}
