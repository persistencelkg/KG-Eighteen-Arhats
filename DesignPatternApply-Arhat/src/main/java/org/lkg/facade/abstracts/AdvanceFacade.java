package org.lkg.facade.abstracts;

import lombok.AllArgsConstructor;
import org.lkg.facade.AirSystem;
import org.lkg.facade.AroundSystem;
import org.lkg.facade.LightSystem;

/**
 * @date: 2025/5/25 17:14
 * @author: li kaiguang
 */
@AllArgsConstructor
public class AdvanceFacade implements CommonFacade{

    private final AirSystem airSystem;

    private final AroundSystem aroundSystem;

    private final LightSystem lightSystem;
    @Override
    public void start() {
        // 三者均可调节
        System.out.println("开始高级影院模式.....");
        aroundSystem.setVoice(100);
        airSystem.setTemperature(25);
        lightSystem.setBrightness(1);
    }

    @Override
    public void end() {
        System.out.println("结束高级影院模式.....");
        aroundSystem.setVoice(10);
        airSystem.setTemperature(30);
        lightSystem.setBrightness(100);
    }
}
