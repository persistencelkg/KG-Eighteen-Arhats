# KG-Eighteen-Arhats
1. 生产应用的武林绝学，以提供开发效率为主
2. 启发优良开发经验的智慧
3. 兴趣所好,持续研发 & 收录中



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
