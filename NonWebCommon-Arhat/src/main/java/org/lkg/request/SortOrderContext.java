package org.lkg.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.lkg.simple.ObjectUtil;

import java.beans.Transient;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/21 2:20 PM
 */
@Data
@AllArgsConstructor
public class SortOrderContext {

    private String field;
    private SortOrderEnum sortOrderEnum;

    @Transient
    public static String sqlOrderBy(SortOrderContext... context) {
        if (ObjectUtil.isEmpty(context)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" order by ");
        for (SortOrderContext sortOrderContext : context) {
            sb.append(String.format("%s %s", sortOrderContext.getField(), sortOrderContext.getSortOrderEnum().getOrder())).append(", ");
        }
        sb.delete(sb.lastIndexOf(","), sb.length());
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(SortOrderContext.sqlOrderBy(new SortOrderContext[]{new SortOrderContext("user", SortOrderEnum.ASC), new SortOrderContext("create_time", SortOrderEnum.DESC)}));
    }
}
