package ru.practicum.ewm.event.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDateTime;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class CheckEventDateValidator implements
        ConstraintValidator<CheckEventDate, LocalDateTime> {

    String parameter;

    @Override
    public void initialize(CheckEventDate checkEventDate) {
        parameter = checkEventDate.parameter();
    }

    @Override
    public boolean isValid(LocalDateTime eventDate,
                           ConstraintValidatorContext context) {
        if (eventDate == null) {
            return true;
        }
        Long hour;
        try {
            hour = Long.parseLong(parameter);
        } catch (NumberFormatException e) {
            return false;
        }
        return eventDate.isAfter(
                LocalDateTime.now().plusHours(hour));
    }
}
