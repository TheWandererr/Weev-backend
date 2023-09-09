package com.pivo.weev.backend.rest.validation.annotation;

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

  String message() default "value must be null or not blank";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
