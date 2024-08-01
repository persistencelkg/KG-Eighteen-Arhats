package org.lkg.request.validator;

import com.google.common.collect.Sets;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class InValidator implements ConstraintValidator<In, Number> {

    private Set<Integer> inValues;

    @Override
    public void initialize(In in) {
        inValues = Sets.newHashSet();
        int[] arr = in.values();
        for (int a : arr) {
            inValues.add(a);
        }
    }

    @Override
    public boolean isValid(Number propertyValue, ConstraintValidatorContext cxt) {
        if (propertyValue == null) {
            return false;
        }
        return inValues.contains(propertyValue.intValue());
    }
}
