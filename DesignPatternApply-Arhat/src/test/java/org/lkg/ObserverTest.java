package org.lkg;

import org.junit.Test;
import org.lkg.behavior_pattern.subject_observer.ConcreteObserverA;
import org.lkg.behavior_pattern.subject_observer.ConcreteObserverB;
import org.lkg.behavior_pattern.subject_observer.ConcreteSubject;

/**
 * @date: 2025/6/15 11:25
 * @author: li kaiguang
 */
public class ObserverTest {

    @Test
    public void testObserver() {
        ConcreteSubject concreteSubject = new ConcreteSubject();

        ConcreteObserverA concreteObserverA = new ConcreteObserverA();
        ConcreteObserverB concreteObserverB = new ConcreteObserverB();

        concreteSubject.attach(concreteObserverB);
        concreteSubject.attach(concreteObserverA);

        concreteSubject.notifyObserver("北京晴转多云");
    }

}
