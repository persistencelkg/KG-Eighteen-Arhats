package org.lkg;

import org.junit.Test;
import org.lkg.structable_pattern.commbine.Directory;
import org.lkg.structable_pattern.commbine.FileComponent;

/**
 * @date: 2025/6/8 11:00
 * @author: li kaiguang
 */
public class CombineTest {

    @Test
    public void testCombine() {
        FileComponent f1 = new FileComponent("a.txt", 19);
        FileComponent f2 = new FileComponent("my.cnf", 333);
        FileComponent f3 = new FileComponent("readme.md", 1024);


        Directory userDir = new Directory("user");
        Directory root = new Directory("root");
        userDir.addComponent(f1);
        userDir.addComponent(f2);

        root.addComponent(f3);
        root.addComponent(userDir);

        root.display();
    }
}
