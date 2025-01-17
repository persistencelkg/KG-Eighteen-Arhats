package org.lkg.utils;

import org.lkg.utils.matcher.CronExpression;

import java.util.Date;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/9 11:00 AM
 */
public class CronUtil {

    public static boolean match(String cron, Date date) {
        if (ObjectUtil.isEmpty(cron) || ObjectUtil.isEmpty(date)) {
            return false;
        }
        try {
            CronExpression exp = new CronExpression(cron);
            return exp.isSatisfiedBy(date);
        }catch (Exception e){
            return false;
        }
    }

    public static boolean matchNow(String cron) {
        return match(cron,new Date());
    }
}
