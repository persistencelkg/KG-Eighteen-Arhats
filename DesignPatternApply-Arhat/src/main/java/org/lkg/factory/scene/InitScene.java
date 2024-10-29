package org.lkg.factory.scene;

import org.lkg.factory.StageContext;
import org.lkg.factory.StageSceneProducerFactory;
import org.lkg.factory.StageStepEnum;
import org.lkg.factory.StageStepProcessor;
import org.lkg.factory.processors.OrderInitialProcessor;
import org.lkg.factory.processors.PayInitialProcessor;

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
