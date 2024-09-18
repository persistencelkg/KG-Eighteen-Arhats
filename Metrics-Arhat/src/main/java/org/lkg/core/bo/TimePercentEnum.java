package org.lkg.core.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/12 7:47 PM
 */

@Getter
@AllArgsConstructor
public enum TimePercentEnum {

    P50(0.5),
    P95(0.95),
    P99(0.99),
    P995(0.995),
    P999(0.999)
    ;

    private double value;


    public static double[] percentValues(){
        return Arrays.stream(TimePercentEnum.values()).mapToDouble(TimePercentEnum::getValue).toArray();
    }
}
