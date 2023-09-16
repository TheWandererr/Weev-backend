package com.pivo.weev.backend.domain.utils;

import java.util.LinkedHashMap;
import java.util.Map;
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

        public static final String FIELD_VALIDATION_FAILED_ERROR_PATTERN = "%s.incorrect";
    }

    @UtilityClass
    public static final class EncryptionPatterns {

        public static final String PRIVATE_KEY_MASK_SYMBOLS = "###";
    }

    @UtilityClass
    public static final class ValidatableFields {

        public static final String LOCAL_START_DATE_TIME = "localStartDateTime";
        public static final String LOCAL_END_DATE_TIME = "localEndDateTime";
    }

    @UtilityClass
    public static final class CompressingParams {

        public static final Double MAX_SCALING = 0.2;
        public static final Map<Long, Double> SCALE_MAPPING;

        static {
            SCALE_MAPPING = new LinkedHashMap<>();
            SCALE_MAPPING.put(1000000L, 0.7);
            SCALE_MAPPING.put(2000000L, 0.6);
            SCALE_MAPPING.put(3000000L, 0.4);
        }
    }
}
