
package org.lkg.core.meter;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.HistogramSupport;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/29 9:51 PM
 */
public abstract class LongHengHistogramSupport extends AbstractMeter implements HistogramSupport {

    protected final HistogramManager histogramManager;

    public LongHengHistogramSupport(Id id, DistributionStatisticConfig distributionStatisticConfig) {
        super(id);
        this.histogramManager = new HistogramManager(distributionStatisticConfig.getPercentiles());
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        return histogramManager.getSnapShot().getInternalSnapshot();
    }



    protected LongHengSnapShot getSnapShot() {
        return histogramManager.getSnapShot();
    }


    public void reset() {
        histogramManager.reset();
    }
}
