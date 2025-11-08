package org.lkg.structable_pattern.facade.fix;

import org.lkg.structable_pattern.facade.AirSystem;
import org.lkg.structable_pattern.facade.AroundSystem;
import org.lkg.structable_pattern.facade.LightSystem;

/**
 * @date: 2025/5/25 17:07
 * @author: li kaiguang
 */
public class FixFacade {

    private final AirSystem airSystem;

    private final AroundSystem aroundSystem;

    private final LightSystem lightSystem;


    public FixFacade() {
        this.airSystem = new AirSystem();
        this.aroundSystem = new AroundSystem();
        this.lightSystem = new LightSystem();
    }

    public void start() {
        System.out.println("影院模式 开始............");
        aroundSystem.setVoice(100);
        airSystem.setTemperature(25);
        lightSystem.setBrightness(1);
    }

    public void end() {
        System.out.println("影院模式 结束............");
        aroundSystem.setVoice(10);
        airSystem.setTemperature(30);
        lightSystem.setBrightness(100);
    }
}
