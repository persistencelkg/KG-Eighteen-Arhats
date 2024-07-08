package org.lkg.elastic_search.crud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.lkg.elastic_search.crud.demo.Orders;
import org.lkg.elastic_search.enums.EsFieldType;
import org.lkg.simple.DateTimeUtils;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class EsMetaApIServiceImpl implements EsMetaApIService<Orders> {
    @SneakyThrows
    @Override
    public boolean createIndex(RestHighLevelClient client, String type, Class<Orders> indexClass) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest();
        Settings build = Settings.builder()
                .put(IndexMetaData.SETTING_NUMBER_OF_SHARDS, 2)
                .put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS, 1).build();

        createIndexRequest.settings(build);
        //
        createIndexRequest.index(ObjectUtil.camelToUnderline(indexClass.getSimpleName()));


        ObjectParser<XContentBuilder, Void> parser = new ObjectParser<>("mappingParser");

        parser.declareString((builder, value) -> {
            try {
                builder.field("type", value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, new ParseField("type"));


        try {
            XContentBuilder mappingBuilder = XContentFactory.jsonBuilder().startObject().startObject("properties");
            for (java.lang.reflect.Field field : indexClass.getDeclaredFields()) {
                mappingBuilder.startObject(ObjectUtil.camelToUnderline(field.getName()));
                // 如何支持keyword
                Class<?> fieldType = field.getType();
                String esType = EsFieldType.getEsType(fieldType);
                ObjectNode jsonNodes = new ObjectNode(JsonNodeFactory.instance);
                if (!Objects.equals(esType, EsFieldType.OBJECT.getEsType())) {
                    jsonNodes.put("type", esType);
                }
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
            createIndexRequest.mapping(type, mappingBuilder);
            log.info("self build es mapping:{} settings:{}", createIndexRequest.mappings(), createIndexRequest.settings());
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            return createIndexResponse.isAcknowledged();
        } catch (IOException e) {
            log.error("self create index fail: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean createIndexWithTemplate(RestHighLevelClient client, String indexPrefix, String type) {
        return false;
    }

    @Override
    public boolean updateTemplate(RestHighLevelClient client, String indexPrefix, String
            type, Orders mapping) {
        return false;
    }

    @Override
    public boolean dropIndex(RestHighLevelClient client, String index) {
        return false;
    }
}
