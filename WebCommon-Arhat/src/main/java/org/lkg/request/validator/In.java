package org.lkg.request.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = InValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface In {
    String message() default "必须在允许的数值内";
    
    int[] values();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
