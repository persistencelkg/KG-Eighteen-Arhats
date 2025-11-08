package org.lkg.behavior_pattern.subject_observer;

/**
 * @date: 2025/6/15 11:20
 * @author: li kaiguang
 * @description: 被观察者的抽象主题
 */
public interface Subject<T> {

    void attach(Observer<T> observer);
    void detach(Observer<T> observer);

    void notifyObserver(T t);
}
