package org.lkg.request;

import lombok.Data;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/7/2 11:51 AM
 */
@Data
public class PageReq {

    private int pageSize = 20;

    private int currentPage = 0;

    private String[] sortField;


    // TODO 排序规则

}
