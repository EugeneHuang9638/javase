package com.eugene.basic.concurrentcollections.unsafe;


import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeOperateObjectTest {


    public static void testNoCAS(final User user) {
        /**
         * 打印:
         * 5
         * 5
         * 5
         * 5
         * 5
         * 10
         * 10
         * 10
         * 10
         * 10
         * 15
         * 15
         * 15
         * 15
         * 15
         *
         * 可以看到每个线程执行到输出user.userId代码时，
         * userId的值已经被五个线程给修改完毕了，此时的userId变成了5,10,15
         * 所以，最终五个线程输出的值都一致
         *
         */
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                while (true) {
                    user.userId++;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(user.userId);
                }
            }, "线程" + i).start();
        }
    }


    /**
     * 直接调用getSafe方法时，会报错，因为在
     * Unsafe.getSafe()方法中，默认要加载bootstrap类加载器时才能
     * 获取到unsafe对象，否则会抛异常
     *
     * 需要恶补下jvm知识
     *
     * @return
     */
    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static void testCAS(final User user) throws NoSuchFieldException {
        final Unsafe unsafe = getUnsafe();
        // 获取要进行cas的字段
        Field userId = User.class.getDeclaredField("userId");

        // 获取字段的偏移量
        long iOffset = unsafe.objectFieldOffset(userId);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {

                while (true) {
                    try {
                        /**
                         * 参数一: 操作的对象
                         * 参数二: 操作对象的属性的偏移量
                         * 参数三: 比较的值，若属性与参数三相等则将参数4的值赋值给对象
                         * 参数四: 要改变的值，然后会将这个值赋值给对象的属性
                         *
                         * CAS原理，就是使用了jvm中的native方法
                         * 它内部会去执行一些cpu的指令，对于cpu而言这些指令是具备原子性的
                         * 也就是说多个线程同时去操作，最后也会只有一个线程去操作
                         *
                         * 而通过一个初始值的比较，比如两个线程同时对i = 0的变量去加1
                         * cpu执行到第一个线程发来的指令时，发现i = 0，于是对他进行加了1，此时i变成了2
                         * 当cpu执行到第二个线程发来的指令时，发现i = 1了，不是初始的i = 0，此时则不会
                         * 执行此指令
                         *
                         * =====>  所以才有了比较的概念，每个线程都拿了初始值和目标值给了cpu，
                         * 当cpu执行对应的指令时，它会将变量现在的值和指令中存的初始值做比较，若一直则进行
                         * 下一步操作，否则不会执行指令。
                         */
                        boolean success = unsafe.compareAndSwapInt(
                                user,
                                iOffset,
                                user.userId ,
                                user.userId + 1);

                        if (success) {
                            // 这样写其实也会有问题的，因为有可能输出的值是另外一个线程修改后的值
                            // System.out.println(user.userId);

                            System.out.println(unsafe.getIntVolatile(user, iOffset));
                        }
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "线程" + i).start();
        }
    }


    public static void main(String[] args) throws Exception {
        final User user = new User();
        // testNoCAS(user);

        testCAS(user);

    }

    private static class User {
        private int userId = 0;
    }
}


