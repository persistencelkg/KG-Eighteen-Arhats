package org.lkg.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/28 9:42 PM
 */

@Data
public class QcHolidayDict {

    private int id;
    private int everyDay;
    private String eachDayName;
    private LocalDateTime createTime;
}
