package com.eugene.basic.volatilestudy;

/**
 * volatile相关测试类
 */
public class View {

    public static void main(String[] args) throws InterruptedException {
        // viewUsable();

        noAtomic();
    }

    private static void noAtomic() {
        Person person = new Person();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int i1 = 0; i1 < 1000; i1++) {
                    person.agePlusPlus();
                }
            }).start();
        }

        /**
         * 一个java程序启动后，至少有两个线程，一个是主线程另外一个是垃圾回收线程
         */
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }

        System.out.println("20个线程对person age变量加加20000次后的结果：" + person.age);
    }

    /**
     * 可见性
     */
    private static void viewUsable() {
        Person person = new Person();
        Thread threadA = new Thread(() -> {
            System.out.println("线程A 3s后修改age....");
            try {
                Thread.sleep(3000);
                person.age = 18;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程A 修改完成： " + person.age);
        }, "线程A");
        System.out.println("主线程等待线程A执行完~~~");
        threadA.start();
        while (person.age == 0) {

        }
        System.out.println("主线程： " + person.age);
    }
}

/**
 * 没有加volatile修饰age的话，程序处于死循环
 * 加了volatile后，程序能正常关闭
 */
class Person {
    /*volatile*/ int age = 0;
    public void addAge(int val) {
        this.age += val;
    }

    public void agePlusPlus() {
        age++;
    }
}
