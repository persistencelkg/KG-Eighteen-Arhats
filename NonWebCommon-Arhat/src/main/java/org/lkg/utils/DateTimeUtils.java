package org.lkg.utils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Description:
 * 1. 各种常见的时间和字符串之间的互相转换
 * 2. 时间戳与日期互转
 * 3. 各个日期之间的单位间隔
 * 4. 兼容Date的日期加减、日期间隔，包括与LocalDateTime之间的互转
 * 5. 使用最新的技术和理论提供设计
 * Author: 李开广
 * Date: 2023/10/16 3:30 PM
 */

public class DateTimeUtils {
    public static final String HH_MM_SS = "HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYY_MM_DD_WITH_BACKSLASH = "yyyy/MM/dd";

    public static final String YYYY_MM_DD_WITH_CHINESE = "yyyy年MM月dd日";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS_SEQ = "yyyyMMddHHmmssSSS";

    public static final String YYYYMMDD_WITH_SHARDING = "yyyyMMdd";
    public static final String YYYYMM_WITH_SHARDING = "yyyyMM";
    public static final String YYYY_WITH_SHARDING = "yyyy";



    public static final String MM_DD = "MM月dd日";

    public static final String TIME_ZONE = "Asia/Shanghai";

    private static final BiFunction<TemporalAccessor, String, String> TIME_CONVERT_TO_STR = (TemporalAccessor time, String pattern) -> DateTimeFormatter.ofPattern(pattern).format(time);
    private static final ZoneOffset CHINA_TIMEZONE = ZoneOffset.ofHours(8);

    public static <T extends TemporalAccessor> T strConvertToTime(String timeStr, String pattern, Class<T> timeClass) {
        if (Objects.isNull(timeStr) || Objects.isNull(pattern) || Objects.isNull(timeClass)) {
            return null;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        if (LocalTime.class == timeClass) {
            return (T) LocalTime.parse(timeStr, fmt);
        } else if (LocalDate.class == timeClass) {
            return (T) LocalDate.parse(timeStr, fmt);
        } else {
            return (T) LocalDateTime.parse(timeStr, fmt);
        }
    }

    public static LocalDate strCoverToDate(String timeStr) {
        return strCovertToDate(timeStr, YYYY_MM_DD);
    }

    public static LocalDate strCovertToDate(String timeStr, String pattern) {
        return strConvertToTime(timeStr, pattern, LocalDate.class);
    }

    public static LocalTime strCovertToTime(String timeStr) {
        return strConvertToTime(timeStr, HH_MM_SS, LocalTime.class);
    }

    public static LocalTime strCovertToTime(String timeStr, String pattern) {
        return strConvertToTime(timeStr, pattern, LocalTime.class);
    }

    public static LocalDateTime strCovertToDateTime(String timeStr, String pattern) {
        return strConvertToTime(timeStr, pattern, LocalDateTime.class);
    }

    public static LocalDateTime strCovertToDateTime(String timeStr) {
        return strConvertToTime(timeStr, YYYY_MM_DD_HH_MM_SS, LocalDateTime.class);
    }

    public static LocalDateTime getLocalDateTime(Object timestamp) {
        LocalDateTime time = null;
        if (timestamp instanceof Timestamp) {
            time = ((Timestamp) timestamp).toLocalDateTime();
        } else if (timestamp instanceof LocalDateTime) {
            time = ((LocalDateTime) timestamp);
        } else if (timestamp instanceof Date) {
            time = convertToLocalDateTime(((Date) timestamp));
        } else if (timestamp instanceof CharSequence) {
            return strCovertToDateTime(timestamp.toString());
        } else if (timestamp instanceof Integer) {
            return secondConvertToTime(Long.valueOf(timestamp.toString()));
        } else if (timestamp instanceof Long) {
            return millSecondConvertToTime(Long.valueOf(timestamp.toString()));
        }
        return time;
    }

    public static String timeConvertToString(TemporalAccessor temporalAccessor, String pattern) {
        if (Objects.isNull(temporalAccessor) || Objects.isNull(pattern)) {
            return null;
        }
        return TIME_CONVERT_TO_STR.apply(temporalAccessor, pattern);
    }

    public static String getCurrentTime() {
        return timeConvertToString(LocalDateTime.now());
    }

    public static String timeConvertToString(TemporalAccessor temporalAccessor) {
        return timeConvertToString(temporalAccessor, YYYY_MM_DD_HH_MM_SS);
    }


    public static Long timeConvertToSecond(LocalDateTime dataTime) {
        return dataTime.toEpochSecond(CHINA_TIMEZONE);
    }

    public static Long timeConvertToMillSecond(LocalDateTime dataTime) {
        return dataTime.toInstant(CHINA_TIMEZONE).toEpochMilli();
    }

    public static LocalDateTime secondConvertToTime(Long second) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(second), CHINA_TIMEZONE);
    }

    public static LocalDateTime millSecondConvertToTime(Long second) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(second), CHINA_TIMEZONE);
    }


    //------------------------------   时间计算类    ----------------------------------

    public static Date dateAdd(Date date, CalendarUnit unit, int value) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(unit.getValue(), value);
        return instance.getTime();
    }

    public static Date dateSub(Date date, CalendarUnit unit, int value) {
        return dateAdd(date, unit, ~value + 1);
    }

    /**
     * 绝对时间差，即不足整数unit单位时按1个unit算
     * 例如<code>dateAbsoluteDiff(d1,d2,CalendarUnit.DAY)</code>
     * 等价计算d1-d2间隔绝对天数，如果不足1天按1天算
     *
     * @param d1   开始时间
     * @param d2   结束时间
     * @param unit
     * @return 有效天数，是绝对概念
     */
    public static long dateAbsoluteDiff(Date d1, Date d2, CalendarUnit unit) {
        if (Objects.isNull(d1) || Objects.isNull(d2) || Objects.isNull(unit)) {
            return -1;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(d1);
        int l1 = instance.get(unit.getValue());
        instance.setTime(d2);
        int l2 = instance.get(unit.getValue());
        return Math.abs(l2 - l1) + 1;
    }

    public static long dayOfAbsoluteDiff(Date d1, Date d2) {
        return dateAbsoluteDiff(d1, d2, CalendarUnit.DAY);
    }

    public static long dayOfRelativeDiff(String d1, String d2) {
        return dateRelativeDiff(convertToDate(strCovertToDateTime(d1)), convertToDate(strCovertToDateTime(d2)), CalendarUnit.DAY);
    }

    public static long dayOfAbsoluteDiff(String d1, String d2) {
        return dayOfAbsoluteDiff(convertToDate(strCovertToDateTime(d1)), convertToDate(strCovertToDateTime(d2)));
    }

    /**
     * 相对时间间隔, 即只考虑整天，不足1天忽略，多用于距离今天数，已过去天数、剩余天数计算
     * @param d1 参数1
     * @param d2 参数2
     * @param unit 单位
     * @return 返回相对间隔，即左闭右开 | 左开右闭的区间数
     */
    public static long dateRelativeDiff(Date d1, Date d2, CalendarUnit unit) {
        return dateAbsoluteDiff(d1, d2, unit) - 1;
    }

    public static long dayOfRelativeDiffNow(Date d1){
        return dateRelativeDiff(d1, new Date(System.currentTimeMillis()), CalendarUnit.DAY);
    }

    public static LocalDateTime convertToLocalDateTime(Date date) {
        return millSecondConvertToTime(date.getTime());
    }

    public static Date convertToDate(LocalDateTime date) {
        return new Date(timeConvertToMillSecond(date));
    }


    public static long getInterval(Temporal s1, Temporal s2, boolean includeS2, ChronoUnit unit) {
        if (Objects.isNull(s1) || Objects.isNull(s2) || Objects.isNull(unit)) {
            return -1;
        }
        long res = unit.between(s1, s2);
        return Math.abs(includeS2 ? res + 1 : res);
    }

    public static long getInterval(Temporal s1, Temporal s2, ChronoUnit unit) {
        return getInterval(s1, s2, false, unit);
    }


    private enum CalendarUnit {
        YEAR(Calendar.YEAR), MONTH(Calendar.MONTH), DAY(Calendar.DAY_OF_MONTH),
        HOUR(Calendar.HOUR_OF_DAY), MINUTE(Calendar.MINUTE), SECOND(Calendar.SECOND),
        MILLS(Calendar.MILLISECOND);
        private final int value;

        CalendarUnit(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(timeConvertToString(now));
        long t = timeConvertToSecond(now);
        System.out.println(t);
        System.out.println(timeConvertToString(secondConvertToTime(t)));

        Date date = new Date(timeConvertToMillSecond(LocalDateTime.of(LocalDate.of(2023, 12, 31), LocalTime.MAX)));
        System.out.println(date);
        System.out.println(dateSub(date, CalendarUnit.DAY, 1));

        System.out.println(getInterval(LocalDate.of(2022, 11, 30), LocalDate.of(2022, 12, 1), false, ChronoUnit.MONTHS));
//        System.out.println(getCommonInterval(LocalTime.of(10,2,33), LocalTime.of(23,33,45), ChronoUnit.SECONDS));
        System.out.println(getInterval(LocalDate.of(2023, 12, 1), LocalDate.of(2022, 11, 30), ChronoUnit.MONTHS));
        Calendar s1 = Calendar.getInstance();
        s1.set(2023, 11, 1, 1, 1, 1);
        Date t1 = s1.getTime();
        System.out.println(t1);
        s1.set(2024, 06, 03, 23, 31, 41);
        Date t2 = s1.getTime();
        System.out.println(t2);
        System.out.println(dateAbsoluteDiff(t1, t2, CalendarUnit.HOUR));
        System.out.println(dayOfRelativeDiffNow(t2));

        timeRelativeTest();
    }

    public static void timeRelativeTest() {
        String now = "2025-02-13 13:00:00";
        String before ="2025-02-11 23:59:59";
        String after = "2025-02-15 23:59:59";
        String nowEnd = "2025-02-13 23:59:59";
        System.out.println("----------相对计算------------");
        System.out.println(dayOfRelativeDiff(before, now));
        System.out.println(dayOfRelativeDiff(after, nowEnd));
        System.out.println(dayOfRelativeDiff(nowEnd, now));
        System.out.println("----------绝对计算------------");
        System.out.println(dayOfAbsoluteDiff(before, now));
        System.out.println(dayOfAbsoluteDiff(after, nowEnd));
        System.out.println(dayOfAbsoluteDiff(nowEnd, now));
    }


}
