package org.lkg.elastic_search.crud;

import org.elasticsearch.client.RestHighLevelClient;

/**
 * Description: ES 元数据操作API
 * Author: 李开广
 * Date: 2024/5/14 8:34 PM
 */
public interface EsMetaApIService<T> {

    /**
     * 通过指定mapping的索引创建
     * 一个数据库，多个索引
     * es 7.0 以后不在支持手动指定type
     *
     * @param client     操作的client
     * @param type       操作的表
     * @param indexClass name为索引名
     * @return
     */
    @Deprecated
    boolean createIndex(RestHighLevelClient client, String type, Class<T> indexClass);


    /**
     * 推荐使用的创建方式
     *
     * @param client
     * @param indexClass
     * @return
     */
    default boolean createIndex(RestHighLevelClient client, Class<T> indexClass) {
        return createIndex(client, "_doc", indexClass);
    }

    /**
     * 通过指定模版的索引创建， 适用于分库场景【es一个mapping只能指定一个表，只能通过模糊库名来实现】
     * 多个数据库，多个索引
     *
     * @param client RestHighLevelClient
     * @param indexPrefix 库前缀
     * @return 是否操作成功
     */
    @Deprecated
    boolean createOrUpdateIndexTemplate(RestHighLevelClient client, String templateName, String indexPrefix, String type, Class<T> indexClass);

    /**
     * 推荐使用方式 按年月日分索引的方式
     * @param client RestHighLevelClient
     * @param templateName 模版名
     * @param indexPrefix  库前缀
     * @param indexClass java DO、POJO
     * @return 是否操作成功
     */
   default boolean createOrUpdateIndexTemplate(RestHighLevelClient client, String templateName, String indexPrefix, Class<T> indexClass) {
        return createOrUpdateIndexTemplate(client, templateName, indexPrefix, "_doc", indexClass);
    }
    boolean dropIndex(RestHighLevelClient client, String index);

}
