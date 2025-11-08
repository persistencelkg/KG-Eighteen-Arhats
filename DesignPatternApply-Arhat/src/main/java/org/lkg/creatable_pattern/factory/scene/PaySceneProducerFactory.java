package org.lkg.creatable_pattern.factory.scene;

import org.lkg.creatable_pattern.factory.StageContext;
import org.lkg.creatable_pattern.factory.StageSceneProducerFactory;
import org.lkg.creatable_pattern.factory.StageStepEnum;
import org.lkg.creatable_pattern.factory.StageStepProcessor;

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
