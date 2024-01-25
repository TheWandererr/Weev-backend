package com.pivo.weev.backend.rest.validation.annotation;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.FILE_FORMAT_ERROR;

import com.pivo.weev.backend.rest.validation.validator.ImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ImageValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {

    String message() default FILE_FORMAT_ERROR;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
