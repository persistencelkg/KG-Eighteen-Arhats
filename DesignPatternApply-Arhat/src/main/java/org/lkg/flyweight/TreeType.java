package org.lkg.flyweight;

import lombok.AllArgsConstructor;

/**
 * @date: 2025/6/8 09:49
 * @author: li kaiguang
 */
@AllArgsConstructor
public class TreeType implements TreeRender{

    private String name;

    private String color;
    @Override
    public void render(int x, int y) {
        System.out.println(name + ":" + color + ",  位置:" + String.format("(%s,%s)", x, y));
    }
}
