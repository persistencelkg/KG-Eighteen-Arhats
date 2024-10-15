package org.lkg.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/14 4:48 PM
 */
@Data
@AllArgsConstructor
public class PageRequest {

    /**
     * 每页大小
     */
    private int pageSize = 10;

    /**
     * 页号
     */
    private int pageIndex;
}
