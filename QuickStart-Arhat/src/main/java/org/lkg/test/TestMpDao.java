package org.lkg.test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lkg.bo.User;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/2 8:29 PM
 */

@Mapper
public interface TestMpDao extends BaseMapper<User> {

}
