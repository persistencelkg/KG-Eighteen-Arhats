package org.lkg.creatable_pattern.factory.scene;

import org.lkg.creatable_pattern.factory.StageContext;
import org.lkg.creatable_pattern.factory.StageSceneProducerFactory;
import org.lkg.creatable_pattern.factory.StageStepEnum;
import org.lkg.creatable_pattern.factory.StageStepProcessor;
import org.lkg.creatable_pattern.factory.processors.OrderInitialProcessor;
import org.lkg.creatable_pattern.factory.processors.PayInitialProcessor;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/29 5:21 PM
 */
public class InitScene implements StageSceneProducerFactory {

    private OrderInitialProcessor initialProcessor;
    private PayInitialProcessor payInitialProcessor;


    @Override
    public StageStepProcessor findProcessor(StageContext stageContext) {
        // 根据stageContext中的业务规则去进行确定扩展
        return null;
    }

    @Override
    public StageStepEnum getEnum() {
        return StageStepEnum.INIT_STEP;
    }
}
