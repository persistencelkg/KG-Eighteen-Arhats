package org.lkg.core;

import com.google.common.base.Function;
import org.lkg.function.ThrowableFunction;
import org.lkg.simple.DateTimeUtils;
import org.lkg.simple.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/23 1:38 PM
 */
public interface AdvanceFunctions{

    Logger log = LoggerFactory.getLogger(AdvanceFunctions.class);

    Function<String, Integer> TO_INT_FUNCTION = input -> {
        if (ObjectUtil.isEmpty(input)) {
            return null;
        }
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    };

    Function<String, Long> TO_LONG_FUNCTION = input -> {
        if (ObjectUtil.isEmpty(input)) {
            return null;
        }
        try {
            return Long.parseLong(input);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    };

    Function<String, Boolean> TO_BOOLEAN_FUNCTION = input -> {
        if (ObjectUtil.isEmpty(input)) {
            return null;
        }
        try {
            return Boolean.parseBoolean(input);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    };

    ThrowableFunction<String, Duration, RuntimeException> STR_TO_DURATION = ref -> {
        if (ObjectUtil.isEmpty(ref)) {
            return null;
        }
        try {
            return Duration.parse(ref);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    };

    ThrowableFunction<String, Period, Exception> STR_TO_PERIOD = Period::parse;

    ThrowableFunction<String, LocalDateTime, Exception> STR_TO_DATETIME = DateTimeUtils::strCovertToDateTime;

    ThrowableFunction<String, LocalDate, Exception> STR_TO_DATE = DateTimeUtils::strCoverToDate;

    ThrowableFunction<String, LocalTime, Exception> STR_TO_TIME = DateTimeUtils::strCovertToTime;

}
