package org.lkg.request;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.beans.Transient;
import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/21 1:55 PM
 */
@Data
public class PageResponse<T> {

    public final static int MAX_RECORD = 5000;

    // 每页多少条
    private int pageSize;

    // 页号
    private int pageIndex;

    // 有多少页
    private int pageCount;

    // 总记录数 max = 10000
    private int totalCount;

    // 是否有下一页
    private boolean hasNext;

    private List<T> itemList;


    public static <T> PageResponse<T> safePage(@Nullable List<T> list, int totalCount, PageRequest pageRequest) {
        if (totalCount > MAX_RECORD) {
            return emptyPage(pageRequest);
        }
        return pageResp(list, totalCount, pageRequest);
    }

    public static <T> PageResponse<T> emptyPage(PageRequest pageRequest) {
        return pageResp(null, 0, pageRequest);
    }

    public static <T> PageResponse<T> pageResp(@Nullable List<T> list, int totalCount, PageRequest pageRequest) {
        PageResponse<T> resp = new PageResponse<>();
        resp.setPageIndex(pageRequest.getPageIndex());
        resp.setPageSize(pageRequest.getPageSize());
        int pageCount = totalCount == 0 ? 0 : (totalCount % pageRequest.getPageSize()) == 0 ?
                totalCount / pageRequest.getPageSize() : totalCount / pageRequest.getPageSize() + 1;
        resp.setPageCount(pageCount);
        resp.setHasNext(pageCount > pageRequest.getPageIndex());
        resp.setItemList(list);
        resp.setTotalCount(totalCount);
        return resp;
    }

}
