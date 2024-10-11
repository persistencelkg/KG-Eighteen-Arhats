package org.lkg.elastic_search.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientProperties;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/11 11:05 AM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomEsRestClientProperties extends ElasticsearchRestClientProperties {

    /**
     * 单机最大连接数100
     */
    private int maxConnectionPerRoute = 100;

    /**
     * 全局连接数1024 ，这取决你的机器数量，假设当前有10台
     */
    private int maxConnectionTotal = 1024;


    /**
     * 对于同步请求：这个控制两个地方： 客户端等待线程操作返回数据的时间，超过无论服务端是否正常返回都会抛出 Listener timeout
     * 一个当检测到返回值为50x 时，会自动重试maxRetryMill - 上一次消耗的时间
     */
    private int maxRetryMill;


    /**
     * 默认1s 容易漏判，导致过了好几秒才被检测到一次，极端情况是到了 maxRetryMill 才被检测
     */
    private int checkTimoutInterval = 100;
}
