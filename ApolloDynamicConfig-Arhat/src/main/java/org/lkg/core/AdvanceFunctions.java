package org.lkg.core;

import com.ctrip.framework.apollo.util.function.Functions;
import org.lkg.function.ThrowableFunction;
import org.lkg.simple.DateTimeUtils;

import java.time.*;
import java.util.function.Function;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/23 1:38 PM
 */
public interface AdvanceFunctions extends Functions {

    ThrowableFunction<String, Duration, RuntimeException> STR_TO_DURATION = ref -> {
        try {
            return Duration.parse(ref);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    };

    ThrowableFunction<String, Period, Exception> STR_TO_PERIOD = Period::parse;

    ThrowableFunction<String, LocalDateTime, Exception> STR_TO_DATETIME = DateTimeUtils::strCovertToDateTime;

    ThrowableFunction<String, LocalDate, Exception> STR_TO_DATE = DateTimeUtils::strCoverToDate;

    ThrowableFunction<String, LocalTime, Exception> STR_TO_TIME = DateTimeUtils::strCovertToTime;

}
