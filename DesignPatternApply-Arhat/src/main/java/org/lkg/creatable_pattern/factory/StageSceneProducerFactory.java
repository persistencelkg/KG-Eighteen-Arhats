package org.lkg.creatable_pattern.factory;

import org.springframework.beans.factory.InitializingBean;

/**
 * Description: 舞台场景，例如跟会员相关factory、跟费用模版相关、 跟押金相关
 * Author: 李开广
 * Date: 2024/10/23 4:56 PM
 */
public interface StageSceneProducerFactory extends InitializingBean {

    StageStepProcessor findProcessor(StageContext  stageContext);

    StageStepEnum getEnum();

    @Override
    default void afterPropertiesSet() throws Exception{
        PriceStage.registry(getEnum(), this);
    }
}
