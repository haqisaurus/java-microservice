package com.example.auth.validation;

import java.util.Calendar;
import java.util.Date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BirthDateValidator implements ConstraintValidator<BirthDate, Date> {

    @Override
    public boolean isValid(Date valueToValidate, ConstraintValidatorContext constraintValidatorContext) {
        Calendar dateInCalendar = Calendar.getInstance();
        dateInCalendar.setTime(valueToValidate);

        return Calendar.getInstance().get(Calendar.YEAR) - dateInCalendar.get(Calendar.YEAR) >= 18;
    }
}
