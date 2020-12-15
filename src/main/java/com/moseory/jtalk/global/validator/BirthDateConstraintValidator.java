package com.moseory.jtalk.global.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Objects;

import static java.time.LocalDate.*;

public class BirthDateConstraintValidator implements ConstraintValidator<BirthDate, LocalDate> {
    public void initialize(BirthDate constraint) {
    }

    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if(Objects.isNull(birthDate))
            return false;

        return birthDate.isAfter(of(1900, 1, 1)) && birthDate.isBefore(now());
    }
}
