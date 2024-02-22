package com.pivo.weev.backend.domain.utils;

import static com.pivo.weev.backend.domain.model.auth.VerificationScope.CHANGE_PASSWORD;
import static com.pivo.weev.backend.domain.model.auth.VerificationScope.DELETE_ACCOUNT;
import static com.pivo.weev.backend.domain.model.auth.VerificationScope.FORGOT_PASSWORD;
import static com.pivo.weev.backend.domain.model.auth.VerificationScope.REGISTRATION;

import com.pivo.weev.backend.domain.model.auth.VerificationScope;
import java.util.Map;
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
    public static final class NotificationTopics {

        public static final String MEET_CONFIRMATION = "meet.confirmation";
        public static final String MEET_UPDATE_SUCCESSFUL = "meet.update.successful";
        public static final String MEET_UPDATE_FAILED = "meet.update.failed";
        public static final String MEET_DECLINATION = "meet.declination";
        public static final String MEET_CANCELLATION = "meet.cancellation";
        public static final String MEET_NEW_JOIN_REQUEST = "meet.new.join.request";
        public static final String MEET_JOIN_REQUEST_CONFIRMATION = "meet.join.request.confirmation";
        public static final String MEET_JOIN_REQUEST_DECLINATION = "meet.join.request.declination";
        public static final String CHAT_CREATED = "chat.created";
        public static final String CHAT_NEW_MESSAGE = "chat.new.message";
    }

    @UtilityClass
    public static final class MessagingPayload {

        public static final String DECLINATION_REASON = "declination_reason";
        public static final String CHAT = "chat";
        public static final String TEXT = "text";
        public static final String MESSAGE = "message";
        public static final String USER = "user";
        public static final String MEET = "meet";
    }

    @UtilityClass
    public static final class NotificationHeaders {

        public static final String TITLE = ".title";
        public static final String BODY = ".body";
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
            public static final String EMAIL_CHANGE_PASSWORD_SUBJECT = "email.change.password.subject";
        }

        @UtilityClass
        public static final class Templates {

            public static final String EMAIL_VERIFICATION_REGISTRATION_FTL = "email-verification-registration.ftl";
            public static final String EMAIL_VERIFICATION_FORGOT_PASSWORD_FTL = "email-verification-forgot-password.ftl";
            public static final String EMAIL_VERIFICATION_CHANGE_PASSWORD_FTL = "email-verification-change-password.ftl";
            public static final String EMAIL_VERIFICATION_DELETE_ACCOUNT_FTL = "email-verification-delete-account.ftl";
            public static final String EMAIL_CHANGE_PASSWORD_FTL = "email-changed-password.ftl";

            public static final Map<VerificationScope, String> VERIFICATION_TEMPLATES_MAPPING = Map.of(REGISTRATION, EMAIL_VERIFICATION_REGISTRATION_FTL,
                                                                                                       FORGOT_PASSWORD, EMAIL_VERIFICATION_FORGOT_PASSWORD_FTL,
                                                                                                       CHANGE_PASSWORD, EMAIL_VERIFICATION_CHANGE_PASSWORD_FTL,
                                                                                                       DELETE_ACCOUNT, EMAIL_VERIFICATION_DELETE_ACCOUNT_FTL);
        }
    }
}
