package org.lkg.elastic_search.crud;

import java.util.Map;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/5/14 8:34 PM
 */
public interface EsMetaApIService {

    boolean createIndex(String indexName, String type);

    boolean createTemplate(String indexPrefix, String type, Map<String, Object> mapping);

    boolean putMapping(String index, Map<String, Object> mapping);

}
