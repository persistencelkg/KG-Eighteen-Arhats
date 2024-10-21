package org.lkg.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/21 2:28 PM
 */
@AllArgsConstructor
@Getter
public enum SortOrderEnum {

    ASC("asc"),

    DESC("desc")

    ;
    private final String order;
}
