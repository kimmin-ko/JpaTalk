package com.moseory.jtalk.global.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        System.out.println("value = " + value);
        return Objects.nonNull(value) && value.matches("[0-9-]+")
                && ((value.length() >= 12) && (value.length() <= 13));
    }

}