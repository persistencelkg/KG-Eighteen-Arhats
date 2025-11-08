package org.lkg.structable_pattern.facade.abstracts;

import lombok.AllArgsConstructor;
import org.lkg.structable_pattern.facade.AirSystem;
import org.lkg.structable_pattern.facade.LightSystem;

/**
 * @date: 2025/5/25 17:12
 * @author: li kaiguang
 */
@AllArgsConstructor
public class BasicFacade implements CommonFacade{

    private AirSystem airSystem;
    private LightSystem lightSystem;
    @Override
    public void start() {
        // 只能调节空调和 灯光
        System.out.println("开始基础影院模式...");
        airSystem.setTemperature(22);
        lightSystem.setBrightness(4);
    }

    @Override
    public void end() {
        // 只能调节空调和 灯光

        System.out.println("结束基础影院模式...");
        airSystem.setTemperature(28);
        lightSystem.setBrightness(99);
    }
}
