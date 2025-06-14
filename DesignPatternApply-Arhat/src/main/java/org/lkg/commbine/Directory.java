package org.lkg.commbine;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2025/6/8 10:57
 * @author: li kaiguang
 */

public class Directory implements Component{
    private String name;
    private List<Component> list = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
    }

    public void addComponent(Component component) {
        list.add(component);
    }

    public void remove(Component  component) {
        list.remove(component);
    }

    @Override
    public void display() {
        System.out.println("目录:" + name);
        for (Component component : list) {
            if (component instanceof  Directory) {
                System.out.println("子目录包含:" + ((Directory) component).name);
            }
            component.display();
        }
    }

    @Override
    public int getSize() {
        int sum = 0;
        for (Component component : list) {
            sum  += component.getSize();
        }
        return sum;
    }
}
