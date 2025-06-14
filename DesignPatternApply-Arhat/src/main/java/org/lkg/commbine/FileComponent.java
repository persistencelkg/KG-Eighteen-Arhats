package org.lkg.commbine;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @date: 2025/6/8 10:55
 * @author: li kaiguang
 */
@AllArgsConstructor
@Getter
public class FileComponent implements Component{

    private String name;

    private int size;

    @Override
    public void display() {
        System.out.println(name + "\t文件大小：" + size + " KB");
    }

    @Override
    public int getSize() {
        return size;
    }
}
