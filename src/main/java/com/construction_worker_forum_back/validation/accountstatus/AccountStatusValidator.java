package com.construction_worker_forum_back.validation.accountstatus;

import com.construction_worker_forum_back.validation.role.ValidRole;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountStatusValidator implements ConstraintValidator<ValidAccountStatus, CharSequence> {
    private List<String> acceptedValues;

    @Override
    public void initialize(ValidAccountStatus annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return acceptedValues.contains(value.toString());
    }
}

