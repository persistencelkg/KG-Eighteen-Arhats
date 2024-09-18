package org.lkg.core.bo;

import lombok.Getter;

@Getter
public enum MeterTypeEnum {
    /**
     * 比较值
     */
    value("值"),
    /**
     * 比较次数
     */
    count("次数"),
    /**
     * 比较总数
     */
    total("总值"),
    p99("p99"),
    p95("p99"),
    p995("p995"),
    max("最大值"),
    min("最小值"),
    /**
     * 比较平均值
     */
    avg("平均值");


    public final String name;

    MeterTypeEnum(String name) {
        this.name = name;
    }


    public static MeterTypeEnum getInstance(String name){
        return MeterTypeEnum.valueOf(name);
    }

    public static void main(String[] args) {
        System.out.println(getInstance("total").getName());
    }
}