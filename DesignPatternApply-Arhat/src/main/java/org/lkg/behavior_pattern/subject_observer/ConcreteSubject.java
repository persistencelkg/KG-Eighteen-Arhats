package org.lkg.behavior_pattern.subject_observer;

import java.util.ArrayList;
import java.util.List;

/**
 * @date: 2025/6/15 11:22
 * @author: li kaiguang
 */
public class ConcreteSubject implements Subject<String> {

    private static final List<Observer<String>> list = new ArrayList<>();

    @Override
    public void attach(Observer<String> observer) {
        list.add(observer);
    }

    @Override
    public void detach(Observer<String> observer) {
        list.remove(observer);
    }

    @Override
    public void notifyObserver(String s) {
        list.forEach(ref -> ref.update(s));
    }
}
