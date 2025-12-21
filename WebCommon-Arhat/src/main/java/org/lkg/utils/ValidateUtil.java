package org.lkg.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

/**
 * @date: 2025/12/14 22:26
 * @author: li kaiguang
 */
public class ValidateUtil {

    private static Validator lowValidator;
    private static jakarta.validation.Validator highValidator;

    static {
        lowValidator = Validation.buildDefaultValidatorFactory().getValidator();
        highValidator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
    }

    public static String  validateRequest(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        if (Objects.isNull(lowValidator)) {
            KgLogUtil.printSysError("lowValidator is null");
            return null;
        }
        Set<ConstraintViolation<Object>> validate = lowValidator.validate(obj);
        StringBuilder stringBuilder = new StringBuilder();
        for (ConstraintViolation<Object> objectConstraintViolation : validate) {
            stringBuilder.append(String.format("%s value:[%s], fail reason:%s", objectConstraintViolation.getPropertyPath(), objectConstraintViolation.getInvalidValue(), objectConstraintViolation.getMessage())).append("\n");
        }
        return stringBuilder.toString();
    }
}
