# DynamicSharding-Arhat
提供基于jdbc的多数据源管理方式，可以灵活扩展任意多个, 使用时需要注意一旦引入该模块，
一定要先配置好数据源，至少1个，避免因为启动扫描bean依赖而导致启动失败


## 核心能力
1. 提供按业务id、时间维度分表算法，支持in查询
2. 提供通用分表算法基础能力，可以通过继承：<code>AbstractHashPreciseShardingTableAlgorithm</code> 定制化自己的分表规则
3. <code>@EnableMoreDynamicDatasource</code>可以控制多数据源是否引入，默认开启
4. 提供基于mybatis#localDateTime解析能力 
5. 通用hash算法的冲突概率和选型