package org.lkg.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lkg.simple.DateTimeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Description: 根据身份证获取年龄
 * Author: 李开广
 * Date: 2024/5/9 3:52 PM
 */
@Slf4j
public class IdCardUtil {
    public static boolean checkIdCard(String idCard) {
        int idCardLength = StringUtils.length(idCard);
        return idCardLength == 15 || idCardLength == 18;
    }


    public static int getAgeByIdCard(String idCard) {
        if (!checkIdCard(idCard)) {
            log.warn("id card invalid :{}", idCard);
            // 取默认值
            return 0;
        }
        // 获取出生日期
        LocalDate birthDay = getBirthDay(idCard);
        // 当前时间
        LocalDate nowTime = LocalDate.now();

        // 计算年龄
        int age = nowTime.getYear() - birthDay.getYear();
        if (nowTime.getMonthValue() < birthDay.getMonthValue()) {
            age = age - 1;

        } else if (nowTime.getMonthValue() == birthDay.getMonthValue()) {
            if (nowTime.getDayOfMonth() <= birthDay.getDayOfMonth()) {
                age = age - 1;
            }
        }

        return age;
    }

    public static LocalDate getBirthDay(String idCard) {
        String birthdayStr;
        if (StringUtils.length(idCard) == 15) {
            birthdayStr = "19" + idCard.substring(6, 8) + "-" + idCard.substring(8, 10) + "-" + idCard.substring(10, 12) ;

        } else {
            birthdayStr = idCard.substring(6, 10) + "-" + idCard.substring(10, 12) + "-" + idCard.substring(12, 14);
        }
        return DateTimeUtils.strCovertToDate(birthdayStr, DateTimeUtils.YYYY_MM_DD);
    }
}
