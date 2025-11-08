
package org.lkg.behavior_pattern.subject_observer;

/**
 * @date: 2025/6/15 11:24
 * @author: li kaiguang
 */
public class ConcreteObserverB implements Observer<String>{
    @Override
    public void update(String s) {
        System.out.println(this.getClass().getName()  + "收到消息:" + s);
    }
}
