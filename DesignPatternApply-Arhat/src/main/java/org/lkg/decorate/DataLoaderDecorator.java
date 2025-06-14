package org.lkg.decorate;

import lombok.AllArgsConstructor;

/**
 * @date: 2025/5/24 21:49
 * @author: li kaiguang
 */
@AllArgsConstructor
public abstract class DataLoaderDecorator implements DataLoader{

    private DataLoader dataLoader;

    @Override
    public String read() {
        return dataLoader.read();
    }

    @Override
    public void write(String str) {
        dataLoader.write(str);
    }
}
