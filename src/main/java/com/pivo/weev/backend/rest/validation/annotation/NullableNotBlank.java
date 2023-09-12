package com.pivo.weev.backend.rest.validation.annotation;

import static com.pivo.weev.backend.rest.utils.Constants.ErrorCodes.MUST_BE_NULL_OR_NOT_BLANK;

import com.pivo.weev.backend.rest.validation.validator.NullableNotBlankValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NullableNotBlankValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullableNotBlank {

  String message() default MUST_BE_NULL_OR_NOT_BLANK;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
