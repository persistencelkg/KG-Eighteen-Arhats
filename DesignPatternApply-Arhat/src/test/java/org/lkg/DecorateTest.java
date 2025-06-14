package org.lkg;

import org.junit.Test;
import org.lkg.decorate.DataLoaderDecorator;
import org.lkg.decorate.DefaultDataLoader;
import org.lkg.decorate.EncryptDataLoader;

/**
 * @date: 2025/5/24 21:54
 * @author: li kaiguang
 */
public class DecorateTest {

    @Test
    public void testDecorate() {
        DefaultDataLoader defaultDataLoader = new DefaultDataLoader();
        defaultDataLoader.write("hhhh");
        String str = defaultDataLoader.read();
        System.out.println(str);
        System.out.println("《《《《《《--------数据加密装饰开始------》》》》");
        EncryptDataLoader encryptDataLoader = new EncryptDataLoader(new DefaultDataLoader());
        encryptDataLoader.write(str);
        System.out.println(encryptDataLoader.read());
    }
}
