package org.lkg.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 价格舞台
 * Author: 李开广
 * Date: 2024/9/18 7:56 PM
 */
public class PriceStage {

    private static final Map<StageStepEnum, StageSceneProducerFactory> MAP = new HashMap<>();

    public static void registry(StageStepEnum stageStepEnum, StageSceneProducerFactory stageSceneProducerFactory) {
        MAP.put(stageStepEnum, stageSceneProducerFactory);
    }

    public static void buildAndProcessStage(StageContext stageContext, StageStepEnum... stageStepEnums) {
        for (StageStepEnum stageStepEnum : stageStepEnums) {
            MAP.get(stageStepEnum).findProcessor(stageContext).process(stageContext);
        }
    }


    public static void calc(StageContext context) {
        buildAndProcessStage(context, StageStepEnum.INIT_STEP, StageStepEnum.CALC_STEP);
    }
}
