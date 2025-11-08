package org.lkg.creatable_pattern.builder;

/**
 * @date: 2025/5/11 17:27
 * @author: li kaiguang
 */
public class ArmComputerBuilder extends BuilderAbstract{
    @Override
    protected void buildMemory() {
     System.out.println("memory build success");
     computer.setMemory("Arm memory");
    }

    @Override
    protected void buildCpu() {
        System.out.println("cpu build success");
        computer.setCpu("Arm cpu");
    }

    @Override
    protected void buildDisk() {
        System.out.println("disk build success");
        computer.setCpu("Arm disk");
    }
}
