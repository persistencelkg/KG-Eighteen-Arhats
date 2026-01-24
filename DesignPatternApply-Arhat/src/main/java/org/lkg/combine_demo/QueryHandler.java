package org.lkg.combine_demo;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @date: 2026/1/22 23:53
 * @author: li kaiguang
 */
public abstract class QueryHandler {
    @Getter
    @Setter
    protected QueryHandler next;

    public abstract void executeQuery(String query);
}
