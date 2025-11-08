package org.lkg.structable_pattern.flyweight;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2025/6/8 09:56
 * @author: li kaiguang
 */
public class Forest {

    private static List<Tree> list = new ArrayList<>();

    public static void addTree(int x, int y, String name, String color) {
        list.add(new Tree(x, y, TreeTypeFactory.getTreeType(name, color)));
    }

    public static void render() {
        for (Tree tree : list) {
            tree.render(tree.getX(), tree.getY());
        }
    }
}
