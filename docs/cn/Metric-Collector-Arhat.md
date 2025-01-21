# Metric-Collector-Arhat

作为全链路metric 采集的集中站，建议单独部署，做好基本高可用即可

1. 内置缓存队列 + kafka双重削峰
2. 支持es、influxdb多种数据存储能力，需要单独部署和引入
3. todo：引入Flink
