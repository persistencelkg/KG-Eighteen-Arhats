package org.lkg.creatable_pattern.prototype;

import lombok.Data;

/**
 * 浅克隆
 * 1. 基于直接内存复制，不调用构造函数，这是jvm规范这样做的好处 对于一些对象的构造函数有复杂逻辑时，这样创建对象可以极大减少开销
 * 2. 执行构造函数还会导致对象属性初始化
 * 3. 本质时逐个字节拷贝，基本类型直接拷贝二进制位，所以新老对象都是互相独立的
 * 4. 引用类型只拷贝指针，也就是地址值，导致拷贝前后的新老对象在发生引用类型属性的修改时，牵一发动全身
 * 5. String引用类型是一个特例，它的的修改彼此是独立的，但这是基于String不可变属性的特性来说，并非浅克隆的原因
 * @date: 2025/5/17 10:21
 * @author: li kaiguang
 */

@Data
public class CloneableTest implements Cloneable {

    private int age;

    // 浅克隆复制string类型时  新对象和元
    private String str;

    private Refer refer;

    @Data
    public static class Refer implements Cloneable{
        private String add;
//
//        @Override
//        public Refer clone() {
//            try {
//                return (Refer) super.clone();
//            } catch (CloneNotSupportedException e) {
//                throw new AssertionError();
//            }
//        }
    }

    public CloneableTest() {
        System.out.println("--构造--");
    }

    @Override
    public CloneableTest clone() throws CloneNotSupportedException {
        CloneableTest clone = (CloneableTest) super.clone();
        // 方式1 ：逐个字段硬编码拷贝
//        Refer refer1 = new Refer();
//        refer1.setAdd(clone.getRefer().getAdd());
//        clone.setRefer(refer1);

        // 方式2：重写引用类型的clone 方法
//        clone.refer = refer.clone();
        return clone;
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        CloneableTest cloneableTest = new CloneableTest();
        cloneableTest.setStr("hhh");
        Refer refer1 = new Refer();
        cloneableTest.setRefer(refer1);
        CloneableTest clone = (CloneableTest) cloneableTest.clone();
        System.out.println(cloneableTest.toString());
        System.out.println(clone);


        clone.getRefer().setAdd("lkg");
        cloneableTest.setAge(33);
        cloneableTest.setStr("222");
        System.out.println(cloneableTest == clone);



        System.out.println(cloneableTest);
        System.out.println(clone);
    }
}
