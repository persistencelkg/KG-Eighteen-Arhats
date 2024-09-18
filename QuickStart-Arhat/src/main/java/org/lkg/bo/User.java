package org.lkg.bo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/9/2 8:20 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    private Long userId;
    private String username;
    private String password;
    private Integer age;
}
