# KG-Eighteen-ArHats
[![License][license-image]][license-url]

**Kg-Eighteen-ArHats**：沉淀了多年在各公司技术建设，几乎覆盖日常开发所有需要的技术点&疑难杂症的解决方案

提供多个功能

* 自定义分布式链路追踪&收集能力，支持任意环节动态注入自定义key-value Paris，更多详情：[Metrics-Arhat接入](/docs/cn/Metric-Arhat.md)
* 基于logback定制化日志收集能力，结合Metric-Arhat 与ELK【ElasticSearch、Logstash、Kibana】实现可视化追踪
* 动态apollo配置获取，支持任意复杂类型key动态解析、支持自定义回调
* 多套数据源整合解决方案，如dynamic-datasource整合sharding，一致性hash解决方案
* 常见分布式多实例整合方案：多套redis、多套kafka、多rocketmq、多es等 & 提供丰富CRUD模版与应用case
* 自建基于钉钉、邮箱通知发送能力
* 强大的基础工具能力如：自定义注解、异常处理、url请求、加密、hash计算、自定义线程池、日期工具等等
* 丰富web业务：上传下载、rpc框架应用、httpclient的定制化开发、预热
* 数据缓存应用解决方案，提供redis、spring Caffeine等应用思路，支持全局统一清理本地缓存
* 常见NoSql应用框架使用：MongoTemplate、HighLevelRestClient、RedisTemplate等
* 自定义重试框架，基于方法的重试能力 支持各大中间件的重试能力
* 提供基于TTL全链路超时熔断机制，避免无效重试，保护下游
* 提供长轮训通用配置模版，简化推拉结合的场景开发
---------


## QuickStart

```xml
 <dependency>
    <groupId>io.github.persistence</groupId>
    <artifactId>EighteenArhat-spring-boot-starter</artifactId>
    <version>${xxx}</version>
</dependency>
```

## NonWeb-Common-Arhat
通用罗汉，以通用工具形式存在各个微服务中，如```apollo通用配置 、 Jackson序列化工具 、 日期计算工具、 金额计算工具、日志工具等```

注：该模块最大的特点就是任何一个文件都是可以轻松移植到其他服务代码中
，即具有通用性和解耦的特点

| 模块            | 应用                             | 备注 |
|---------------|--------------------------------|----|
| JacksonUtil   | 序列化                            |    |
| SimpleRequest | 基于java内置URL请求，适用快速请求，无连接池应用的场景 |    |
|               |                                |    |




## Web-Common-Arhat
web罗汉，提供了RPC【OpenFeign】、通用HTTP请求【包括可配置化的HttpClient、OkHttp、Rest Template】



| 模块          | 应用                                                                   | 备注 |
|-------------|----------------------------------------------------------------------|----|
| RPC         | OpenFeign ，支持动态修改服务名、URl、                                            |    |
| HTTP        | 支持HttpClient、OkHttp、Rest Template，可以基于服务、url进行细粒度动态配置超时时间、重试次数等<br/> |    |
| UP_DOWNLOAD | 基于EasyUtil、Apache实现文件导出、上传下载                                         |    |
| ORM         | 支持MyBatis、Mybatis Plus、 JPA                                          |    |
| Authentic   | 支持JWT、SpringSecurity、Shiro进行权限认证                                     |    |



## MQ-Arhat
MQ罗汉，提供各个中大厂常用的消息队列中间件，提供通用生产者工具、消费者工具； 包括但不限于```通过各种重试提供可靠交付、延时队列、异步同步发送等```



[license-image]: https://img.shields.io/badge/license-Apache%202-4EB1BA.svg
[license-url]: https://www.apache.org/licenses/LICENSE-2.0.html