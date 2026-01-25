package org.lkg.combine_demo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @date: 2026/1/22 23:51
 * @author: li kaiguang
 */
public class QueryChain{

    private QueryHandler startHandler;

    @Resource
    private CacheQueryHandler cacheQueryHandler;

    @Resource
    private RealTimeQueryHandler realTimeQueryHandler;



    @PostConstruct
    public void init() {
        this.cacheQueryHandler.setNext(realTimeQueryHandler);
        this.startHandler = cacheQueryHandler;
    }

    public void executeQuery(String query) {
        startHandler.executeQuery(query);
    }
}
