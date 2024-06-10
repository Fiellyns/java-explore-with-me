package ru.practicum.ewm.event.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = CheckEventDateValidator.class)
@Target(ElementType.FIELD)
@Documented
public @interface CheckEventDate {

    String message() default "EventDate должен быть не ранее, чем через 2 часа от текущего времени";

    String parameter() default "2";

    Class<?>[] groups() default {};

    Class<? extends Payload> [] payload() default {};
}