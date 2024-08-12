package org.lkg.request.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = BigDecimalCheckValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BigDecimalCheck {
    String message() default "";

    int scale() default  2;

    double max() default Double.MAX_VALUE;

    double min() default 0;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
