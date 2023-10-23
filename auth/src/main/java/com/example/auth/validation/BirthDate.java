package com.example.auth.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = BirthDateValidator.class)
@Target({  ElementType.FIELD, })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BirthDate {
    String message() default "Custom validation error message";
    Class <?> [] groups() default {};
    Class <? extends Payload> [] payload() default {};

}
