package org.lkg.elastic_search.crud;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.lkg.elastic_search.config.MoreEsClient;
import org.lkg.elastic_search.enums.EsFieldType;
import org.lkg.elastic_search.enums.TextIndex;
import org.lkg.utils.DateTimeUtils;
import org.lkg.utils.ObjectUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Service
@Slf4j
public class EsMetaApIServiceImpl implements EsMetaApIService {


    @Override
    public Map<String, Object> getMappingFieldList(RestHighLevelClient client, String index, String type) {
        GetMappingsRequest getIndexRequest = new GetMappingsRequest();
        getIndexRequest.indices(index);
        try {
            GetMappingsResponse mapping = client.indices().getMapping(getIndexRequest, RequestOptions.DEFAULT);
            ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = mapping.getMappings();
            ImmutableOpenMap<String, MappingMetaData> mappingMap = mappings.get(index);
            if (Objects.isNull(mappingMap) || Objects.isNull(mappingMap.getOrDefault(type, mappingMap.get(DEFAULT_TYPE)))) {
                return null;
            }
            MappingMetaData orDefault = mappingMap.getOrDefault(type, mappingMap.get(DEFAULT_TYPE));
            return orDefault.getSourceAsMap();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean existIndex(RestHighLevelClient client, Class<?> index) {
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(ObjectUtil.camelToUnderline(index.getSimpleName()));
        try {
            return client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @SneakyThrows
    @Override
    public boolean createIndex(RestHighLevelClient client, String type, Class<?> indexClass) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest();
        // 不建议通过程序控制，最好在kibana创建索引的同时指定settings
        Settings build = Settings.builder()
                .put(IndexMetaData.SETTING_NUMBER_OF_SHARDS, 2)
                .put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS, 1).build();
//        createIndexRequest.timeout("5ms");
        createIndexRequest.settings(build);
        // 索引名的规则必须是小写，以及其他说明 参照 https://www.elastic.co/guide/en/elasticsearch/reference/6.4/indices-create-index.html
        createIndexRequest.index(ObjectUtil.camelToUnderline(indexClass.getSimpleName()));
        try {

            XContentBuilder mappingBuilder = autoParseClassTypeForBuilder(indexClass.getDeclaredFields());
            createIndexRequest.mapping(type, mappingBuilder);
            log.info("self build es mapping:{} settings:{}", createIndexRequest.mappings(), createIndexRequest.settings());
            // 一般来说改方式创建都容易超时，为了不影响正在使用的业务，最好异步处理
//            client.indices().createAsync(createIndexRequest, RequestOptions.DEFAULT, new ActionListener<CreateIndexResponse>() {
//                @Override
//                public void onResponse(CreateIndexResponse createIndexResponse) {
//                    log.info("create index result:{}", createIndexResponse.isAcknowledged());
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    log.error(e.getMessage(), e);
//                }
//            });
            return  client.indices().create(createIndexRequest, RequestOptions.DEFAULT).isAcknowledged();
        } catch (Exception e) {
            log.error("self create index fail: {}", e.getMessage(), e);
        }
        return false;
    }

    private XContentBuilder autoParseClassTypeForBuilder(Field[] fields) throws IOException {
        ObjectParser<XContentBuilder, Void> parser = new ObjectParser<>("mappingParser");
        // es 类型元数据的定义
        parser.declareString((builder, value) -> {
            try {
                builder.field("type", value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, new ParseField("type"));
        // 封装请求模版
        XContentBuilder mappingBuilder = XContentFactory.jsonBuilder().startObject().startObject("properties");
        for (java.lang.reflect.Field field : fields) {
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            mappingBuilder.startObject(ObjectUtil.camelToUnderline(field.getName()));
            // 如何支持keyword
            Class<?> fieldType = field.getType();
            String esType = EsFieldType.getEsType(fieldType);
            TextIndex annotation = field.getAnnotation(TextIndex.class);
            if (Objects.nonNull(annotation)) {
                esType = annotation.value().getEsType();
            }
            // 暂不支持object类型 但是支持nested
            if (Objects.equals(esType, EsFieldType.OBJECT.getEsType())) {
                continue;
            }
            ObjectNode jsonNodes = new ObjectNode(JsonNodeFactory.instance);
            jsonNodes.put("type", esType);
            if (Objects.equals(esType, EsFieldType.DATE.getEsType())) {
                String format = "format";
                parser.declareString((builder, value) -> {
                    try {
                        builder.field(format, value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, new ParseField(format));
                jsonNodes.put(format, DateTimeUtils.YYYY_MM_DD_HH_MM_SS_SSS);
            }
            // 精度浮点数
            if (Objects.equals(esType, EsFieldType.SCALE_FLOAT.getEsType())) {
                String format = "scaling_factor";
                parser.declareInt((builder, value) -> {
                    try {
                        builder.field(format, value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, new ParseField(format));
                jsonNodes.put(format, 100);
            }
            parser.parse(JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, jsonNodes.toString()),
                    mappingBuilder, null);
            mappingBuilder.endObject();
        }

        mappingBuilder.endObject().endObject();
        return mappingBuilder;
    }


    @Override
    public boolean addColumnForIndex(RestHighLevelClient client, String type, Class<?> indexClass) {
        String index = ObjectUtil.camelToUnderline(indexClass.getSimpleName());
        // 查看现有的，是否有冲突
        Map<String, Object> mappingFieldList = getMappingFieldList(client, index, type);
        log.info("found mapping:{}", mappingFieldList);
        Field[] declaredFields = indexClass.getDeclaredFields();
        List<Field> newFieldList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(mappingFieldList)) {
            Map<String, Object> map = (Map<String, Object>) mappingFieldList.get("properties");
            for (Field declaredField : declaredFields) {
                if (Modifier.isFinal(declaredField.getModifiers()) || Modifier.isStatic(declaredField.getModifiers())) {
                    continue;
                }
                if (!map.containsKey(ObjectUtil.camelToUnderline(declaredField.getName()))) {
                    newFieldList.add(declaredField);
                }
            }
        }
        if (newFieldList.isEmpty()) {
            log.warn("not found new field to add");
            return false;
        }
        PutMappingRequest putMappingRequest = new PutMappingRequest();
        putMappingRequest.type(type);
        putMappingRequest.indices(index);
        try {
            log.info("found new add column:{}",newFieldList);
            putMappingRequest.source(autoParseClassTypeForBuilder(newFieldList.toArray(new Field[0])));
            return client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT).isAcknowledged();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean createOrUpdateIndexTemplate(RestHighLevelClient client, String templateName, String indexPrefix, String type, Class<?> indexClass) {
        PutIndexTemplateRequest putIndexTemplateRequest = new PutIndexTemplateRequest(templateName);
        // Settings with default
        // 不建议通过程序控制，最好在kibana创建索引的同时指定settings, 需要结合节点的时机数量去处理
        Settings build = Settings.builder()
                .put(IndexMetaData.SETTING_NUMBER_OF_SHARDS, MoreEsClient.defaultEsIndexNumOfShards)
                .put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS, MoreEsClient.defaultEsIndexNumanReplica).build();

        putIndexTemplateRequest.settings(build);
        putIndexTemplateRequest.patterns(Collections.singletonList(indexPrefix));
        try {
            putIndexTemplateRequest.mapping(type, autoParseClassTypeForBuilder(indexClass.getDeclaredFields()));
            // template 不推荐在单测环境使用异步，因为异步的结果可能还没返回，就已经中止测试了
            PutIndexTemplateResponse putIndexTemplateResponse = client.indices().putTemplate(putIndexTemplateRequest, RequestOptions.DEFAULT);
            return putIndexTemplateResponse.isAcknowledged();
        } catch (Exception e) {
            log.error("self create template fail, template:{} reason:{}", templateName, e.getMessage(), e);
        }
        return false;

    }


    @Override
    public boolean dropIndex(RestHighLevelClient client, String index) {
        DeleteIndexRequest orderDeleteRequest = new DeleteIndexRequest(index);
        try {
            DeleteIndexResponse delete = client.indices().delete(orderDeleteRequest, RequestOptions.DEFAULT);
            log.info("index:{} has drop", index);
            return delete.isAcknowledged();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
