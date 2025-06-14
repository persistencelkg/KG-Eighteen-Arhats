package org.lkg;

import org.junit.Test;
import org.lkg.facade.AirSystem;
import org.lkg.facade.AroundSystem;
import org.lkg.facade.LightSystem;
import org.lkg.facade.fix.FixFacade;

/**
 * @date: 2025/5/25 17:16
 * @author: li kaiguang
 */
public class FacadeTest {

    @Test
    public void testFixFacade() {
        AirSystem airSystem = new AirSystem();
        AroundSystem aroundSystem = new AroundSystem();
        LightSystem lightSystem = new LightSystem();
        aroundSystem.setVoice(100);
        airSystem.setTemperature(25);
        lightSystem.setBrightness(1);



        System.out.println("-----使用facade---之后");
        FixFacade fixFacade = new FixFacade();
        fixFacade.start();
        fixFacade.end();
    }
}
