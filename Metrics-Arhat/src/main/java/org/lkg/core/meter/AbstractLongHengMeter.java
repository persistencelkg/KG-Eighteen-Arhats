package org.lkg.core.meter;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Measurement;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/29 9:55 PM
 */
public class AbstractLongHengMeter extends AbstractMeter {


    public AbstractLongHengMeter(Id id) {
        super(id);
    }

    @Override
    public Iterable<Measurement> measure() {
        return null;
    }
}
