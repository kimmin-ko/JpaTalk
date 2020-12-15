package com.moseory.jtalk.global.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD, METHOD})
@Constraint(validatedBy = BirthDateConstraintValidator.class)
public @interface BirthDate {
    String message() default "잘못된 형식의 생년월일입니다. (1900년 1월 1일 ~ 현재 사이의 날짜)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
