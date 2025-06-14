package org.lkg;

import org.junit.Test;
import org.lkg.flyweight.Forest;
import org.lkg.flyweight.TreeTypeFactory;

import java.util.Random;

/**
 * @date: 2025/6/8 09:58
 * @author: li kaiguang
 */
public class FlyWeightTest {

    @Test
    public void testCreateForest() {
        Random random = new Random();
        String[] name = new String[]{"桃树", "白桦树", "梧桐树", "苹果树"};
        String[] color = new String[]{"红色", "绿色", "黄色"};
        for (int i = 0; i < 10000 ; i++) {
            int x = random.nextInt(1000);
            int y = random.nextInt(1000);
            int randColor = random.nextInt(color.length);
            int randName = random.nextInt(name.length);
            Forest.addTree(x, y , name[randName], color[randColor]);
        }

        Forest.render();
        System.out.println("树类型个数:" + TreeTypeFactory.getSize());
    }
}
