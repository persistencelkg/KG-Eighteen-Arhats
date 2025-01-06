package org.lkg.algorithm;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.provider.AbstractDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 解决："Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required"
 * 具体:<a href="https://blog.csdn.net/lkg5211314/article/details/130899735?spm=1001.2014.3001.5502"></a>
 * Description:
 * Author: 李开广
 * Date: 2024/9/18 4:52 PM
 */
@Configuration
// 这个目的是不和自带的配置注入shardingDataSource 共享
@EnableMoreDynamicDatasource
@AutoConfigureBefore({DynamicDataSourceAutoConfiguration.class})
public class DynamicDataSourceCompatibleShardingConfiguration {

    public static final String SHARDING_DATASOURCE = "sharding";

    @Resource
    private DynamicDataSourceProperties dynamicDataSourceProperties;
    /**
     * shardingjdbc有四种数据源，需要根据业务注入不同的数据源
     *
     * <p>1. 未使用分片, 脱敏的名称(默认): shardingDataSource;
     * <p>2. 主从数据源: masterSlaveDataSource;
     * <p>3. 脱敏数据源：encryptDataSource;
     * <p>4. 影子数据源：shadowDataSource
     *
     * shardingjdbc默认就是shardingDataSource
     */
    @Lazy
    @Resource(name = "shardingDataSource")
    private DataSource shardingDataSource;

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider() {
        // 多数据源的map
        Map<String, DataSourceProperty> datasource = dynamicDataSourceProperties.getDatasource();
        // 接管sharding
        return  new AbstractDataSourceProvider() {
            @Override
            public Map<String, DataSource> loadDataSources() {
                Map<String, DataSource> dataSourceMap = createDataSourceMap(datasource);
                dataSourceMap.put(SHARDING_DATASOURCE, shardingDataSource);
                return dataSourceMap;
            }
        };
    }

    @Primary // 关键
    @Bean
    public DataSource dataSource(DynamicDataSourceProvider dynamicDataSourceProvider) {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        dynamicRoutingDataSource.setP6spy(dynamicDataSourceProperties.getP6spy());
        dynamicRoutingDataSource.setPrimary(dynamicDataSourceProperties.getPrimary());
        dynamicRoutingDataSource.setSeata(dynamicDataSourceProperties.getSeata());
        dynamicRoutingDataSource.setStrict(dynamicDataSourceProperties.getStrict());
        dynamicRoutingDataSource.setStrategy(dynamicDataSourceProperties.getStrategy());
        return dynamicRoutingDataSource;
    }

}
