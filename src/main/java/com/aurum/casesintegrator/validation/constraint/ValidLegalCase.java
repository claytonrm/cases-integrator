package com.aurum.casesintegrator.validation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.aurum.casesintegrator.validation.validator.CaseValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {CaseValidator.class})
@Documented
public @interface ValidLegalCase {

    String message() default "String does not match a valid legal case";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
