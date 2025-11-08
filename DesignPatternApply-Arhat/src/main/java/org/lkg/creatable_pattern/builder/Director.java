package org.lkg.creatable_pattern.builder;

import lombok.AllArgsConstructor;

/**
 * @date: 2025/5/11 17:30
 * @author: li kaiguang
 */
@AllArgsConstructor
public class Director {
    private BuilderAbstract builderAbstract;



    public Computer builderComputer() {
        return builderAbstract.buildComputer();
    }


    public static void main(String[] args) {
        System.out.println(new Director(new IntelComputerBuilder()).builderComputer());
    }
}
