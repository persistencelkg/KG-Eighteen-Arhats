# Metrics-ArHats
作为APM中的metrics，
1. 需要收集器不端根据配置进行轮询
2. 上报能力有限

基于micrometer重写部分指标收集器，如LongHengTimer；
旨在提供更灵活的配置，可以定制化指标发送与收集，并且可以可控发送的频率，
