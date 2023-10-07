package com.pivo.weev.backend.domain.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {

    @UtilityClass
    public static final class ErrorCodes {

        public static final String PKCS8_KEY_PASSPHRASE_NOT_FOUND_ERROR = "pkcs8.key.password.not.found";
        public static final String PKCS8_KEY_MASKED_VALUE_NOT_FOUND_ERROR = "pkcs8.key.masked.value.not.found";
        public static final String AUTHENTICATION_PRINCIPAL_NOT_FOUND_ERROR = "authentication.principal.not.found";
        public static final String AUTHORIZATION_TOKEN_NOT_FOUND_ERROR = "authorization.token.not.found";
        public static final String CLOUD_OPERATION_ERROR = "cloud.uploading.failed";
        public static final String FILE_COMPRESSING_ERROR = "file.compressing.failed";
        public static final String SUBCATEGORY_NOT_FOUND_ERROR = "subcategory.not.found";
        public static final String TIME_ZONE_ID_NOT_FOUND_ERROR = "time.zone.id.not.recognized";
        public static final String TOKEN_COMPROMISED_ERROR = "token.compromised";

        public static final String FIELD_VALIDATION_FAILED_ERROR_PATTERN = "%s.incorrect";

        public static final String EVENT_MODERATION_IMPOSSIBLE_ERROR = "event.moderation.impossible";

        public static final String ACCESS_DENIED_ERROR = "access.denied";
    }

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

        public static final String EVENT_CONFIRMATION = "event.confirmation";
        public static final String EVENT_UPDATE_SUCCESSFUL = "event.update.successful";
        public static final String EVENT_UPDATE_FAILED = "event.update.failed";
        public static final String EVENT_DECLINATION = "event.declination";
        public static final String EVENT_CANCELLATION = "event.cancellation";
    }
}
