package org.lkg.factory.scene;

import org.lkg.factory.StageContext;
import org.lkg.factory.StageSceneProducerFactory;
import org.lkg.factory.StageStepEnum;
import org.lkg.factory.StageStepProcessor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/29 7:11 PM
 */
public class PaySceneProducerFactory implements StageSceneProducerFactory {

    // pay 1 processor

    // pay 2 processor

    @Override
    public StageStepProcessor findProcessor(StageContext stageContext) {
        // divide processor
        return null;
    }

    @Override
    public StageStepEnum getEnum() {
        return StageStepEnum.CALC_STEP;
    }
}
