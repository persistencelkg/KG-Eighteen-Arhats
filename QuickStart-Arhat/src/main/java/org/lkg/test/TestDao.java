package org.lkg.test;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.lkg.bo.QcHolidayDict;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/1 3:00 PM
 */
@Component
@Mapper
public interface TestDao{


    List<QcHolidayDict> listData(@Param("id") int id);

}
