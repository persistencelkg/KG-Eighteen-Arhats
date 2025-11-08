package org.lkg.structable_pattern.decorate;

/**
 * @date: 2025/5/24 21:48
 * @author: li kaiguang
 */
public class DefaultDataLoader implements DataLoader{

    private String str;

    @Override
    public String read() {
        return str;
    }

    @Override
    public void write(String str) {
        System.out.println("默认数据写入: " + str );
        this.str = str;
    }
}
