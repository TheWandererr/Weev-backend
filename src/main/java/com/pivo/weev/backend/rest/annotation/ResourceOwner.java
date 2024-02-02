package com.pivo.weev.backend.rest.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD})
@Retention(RUNTIME)
public @interface ResourceOwner {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
