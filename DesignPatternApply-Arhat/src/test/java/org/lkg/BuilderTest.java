package org.lkg;

import org.junit.Test;
import org.lkg.creatable_pattern.builder.common_builder.CommonComputer;

/**
 * @date: 2025/5/11 17:52
 * @author: li kaiguang
 */
public class BuilderTest {


    @Test
    public void testCommonBuilder() {
        CommonComputer build = CommonComputer.builder().cpu("cpu").disk("disk").memory("memory").build();
        System.out.println(build);
    }
}
