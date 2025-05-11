package org.lkg;

import org.junit.Test;
import org.lkg.singleton.EnumSingleton;
import org.lkg.singleton.HungrySingleton;
import org.lkg.singleton.InnerClassSingleton;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @date: 2025/5/10 17:00
 * @author: li kaiguang
 */
public class SingletonTest {

    @Test
    public void testSingleTon() {
        System.out.println(HungrySingleton.getInstance() == HungrySingleton.getInstance());
    }

    @Test
    public void testSerializeDestroySingleton() throws IOException, ClassNotFoundException {
        String fileName = "singleton.txt";
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(new File(fileName).toPath()));
        objectOutputStream.writeObject(InnerClassSingleton.getInstance());

        ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(new File(fileName).toPath()));
        Object o = objectInputStream.readObject();
        System.out.println(InnerClassSingleton.getInstance() == o);
    }

    @Test
    public void testEnumSafety() throws IOException, ClassNotFoundException {
        String fileName = "singleton-enum.txt";
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(new File(fileName).toPath()));
        objectOutputStream.writeObject(EnumSingleton.getInstance());

        ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(new File(fileName).toPath()));
        Object o = objectInputStream.readObject();
        System.out.println(EnumSingleton.getInstance() == o);
    }
}
