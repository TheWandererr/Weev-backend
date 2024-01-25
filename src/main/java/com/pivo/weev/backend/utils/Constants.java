package com.pivo.weev.backend.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    @UtilityClass
    public static final class Symbols {

        public static final String DASH = "-";
        public static final String DOT = ".";
        public static final String SLASH = "/";
        public static final String COLON = ":";
    }

    @UtilityClass
    public static final class Amount {

        public static final Integer INFINITY = -1;
    }

    @UtilityClass
    public static final class ErrorCodes {

        public static final String CREDENTIALS_ERROR = "bad.credentials";
        public static final String PERMISSIONS_ERROR = "not.enough.permissions";
        public static final String UNAUTHORIZED_ERROR = "unauthorized";
        public static final String FIELD_VALIDATION_FAILED_ERROR = "field.validation.failed";
        public static final String MUST_BE_NOT_NULL_ERROR = "must.be.not.null";
        public static final String MUST_BE_NOT_BLANK_ERROR = "must.be.not.blank";
        public static final String MUST_BE_NULL_OR_NOT_BLANK_ERROR = "must.be.null.or.not.blank";
        public static final String INVALID_LENGTH_ERROR = "invalid.length";
        public static final String INVALID_AMOUNT_ERROR = "invalid.amount";
        public static final String LENGTH_OUT_OF_BOUND_ERROR = "length.out.of.bound";
        public static final String FILE_FORMAT_ERROR = "invalid.file";
        public static final String FLOW_INTERRUPTED_ERROR = "flow.interrupted.error";
        public static final String DECLINATION_REASON_UNSUPPORTED_ERROR = "unsupported.declination.reason";
        public static final String ID_FORMAT_ERROR = "invalid.id";
        public static final String TOKEN_ERROR = "invalid.token";
        public static final String PKCS8_KEY_PASSPHRASE_NOT_FOUND_ERROR = "pkcs8.key.password.not.found";
        public static final String PKCS8_KEY_MASKED_VALUE_NOT_FOUND_ERROR = "pkcs8.key.masked.value.not.found";
        public static final String AUTHENTICATION_PRINCIPAL_NOT_FOUND_ERROR = "authentication.principal.not.found";
        public static final String AUTHORIZATION_TOKEN_NOT_FOUND_ERROR = "authorization.token.not.found";
        public static final String CLOUD_OPERATION_ERROR = "cloud.uploading.failed";
        public static final String FILE_COMPRESSING_ERROR = "file.compressing.failed";
        public static final String SUBCATEGORY_NOT_FOUND_ERROR = "subcategory.not.found";
        public static final String TIME_ZONE_ID_NOT_FOUND_ERROR = "time.zone.id.not.recognized";
        public static final String TOKEN_COMPROMISED_ERROR = "token.compromised";
        public static final String TEMPLATE_PROCESSING_ERROR = "template.processing.failed";
        public static final String ACTIVE_VERIFICATION_REQUEST_ERROR = "active.verification.request.exists";
        public static final String NO_VERIFICATION_REQUEST_ERROR = "no.verification.request";
        public static final String VERIFICATION_REQUEST_IS_EXPIRED_ERROR = "verification.request.is.expired";
        public static final String VERIFICATION_CODE_ERROR = "verification.code.incorrect";

        public static final String FIELD_VALIDATION_FAILED_ERROR_PATTERN = "%s.incorrect";

        public static final String ACCESS_DENIED_ERROR = "access.denied";
        public static final String OPERATION_IMPOSSIBLE_ERROR = "operation.impossible";
    }

    @UtilityClass
    public static final class Reasons {

        public static final String EVENT_CAPACITY_EXCEEDED = "event.capacity.exceeded";

    }
}
