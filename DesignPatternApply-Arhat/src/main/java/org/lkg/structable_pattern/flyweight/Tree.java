package org.lkg.structable_pattern.flyweight;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @date: 2025/6/8 09:49
 * @author: li kaiguang
 */
@AllArgsConstructor
@Getter
public class Tree implements TreeRender {

    private int x;
    private int y;
    private TreeType treeType;

    @Override
    public void render(int x, int y) {
        treeType.render(x, y);
    }
}
