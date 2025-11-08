package org.lkg.creatable_pattern.builder;

/**
 * @date: 2025/5/11 17:24
 * @author: li kaiguang
 */
public abstract class BuilderAbstract {

    protected Computer computer = new Computer();

    protected abstract void buildMemory();

    protected abstract void buildCpu();

    protected abstract  void buildDisk();

    public Computer buildComputer() {
        buildCpu();
        buildDisk();
        buildMemory();
        return computer;
    }
}
