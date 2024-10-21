package org.lkg.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.beans.Transient;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/14 4:48 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    public static final int MAX_PAGE_SIZE = 50;

    /**
     * 每页大小
     */
    @Max(value = MAX_PAGE_SIZE, message = "too much size")
    @Min(value = 1, message = "invalid size")
    private int pageSize = 10;

    /**
     * 页号
     */
    @Max(value = PageResponse.MAX_RECORD / MAX_PAGE_SIZE, message = "too long page")
    @Min(value = 0,message = "invalid index")
    private int pageIndex;

    private SortOrderContext[] sortOrderContext;


    @Transient
    public String sqlLimit() {
        return String.format(" limit %s,%s ", (this.pageIndex - 1) * this.pageSize, this.pageSize);
    }

    public static void main(String[] args) {
        System.out.println(new PageRequest());
    }
}
