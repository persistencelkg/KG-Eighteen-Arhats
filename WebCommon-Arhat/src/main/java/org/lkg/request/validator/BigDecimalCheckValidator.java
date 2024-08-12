package org.lkg.request.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/1 4:17 PM
 */
public class BigDecimalCheckValidator implements ConstraintValidator<BigDecimalCheck, BigDecimal> {

    private int scale;
    private BigDecimal min;
    private BigDecimal max;

    @Override
    public void initialize(BigDecimalCheck constraintAnnotation) {
        this.scale = constraintAnnotation.scale();
        this.max = BigDecimal.valueOf(constraintAnnotation.max());
        this.min = BigDecimal.valueOf(constraintAnnotation.min());
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return false;
        }
        return value.scale() <= scale && value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }
}
