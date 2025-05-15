package org.lkg.builder.common_builder;

import lombok.Getter;
import lombok.ToString;

/**
 * @date: 2025/5/11 17:38
 * @author: li kaiguang
 */
@ToString
@Getter
public class CommonComputer {
    private String memory;

    private String cpu;

    private String disk;

    private String mouse;

    private CommonComputer(){
        System.out.println("111");
    }

    public static class Builder {
        private final CommonComputer computerBuilder = new CommonComputer();

        public Builder memory(String memory) {
            computerBuilder.memory = memory;
            return this;
        }

        public Builder disk(String disk) {
            computerBuilder.disk = disk;
            return this;
        }

        public Builder cpu(String cpu) {
            computerBuilder.cpu = cpu;
            return this;
        }

        public Builder mouse(String mouse) {
            computerBuilder.mouse = mouse;
            return this;
        }

        public CommonComputer build() {
            return computerBuilder;
        }
    }
}
