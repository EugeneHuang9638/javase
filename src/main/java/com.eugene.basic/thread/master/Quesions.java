package com.eugene.basic.thread.master;

/**
 * 解决几个"奇怪"的问题
 *
 */
public class Quesions {


    /**
     * 此种情况下，程序能够正常退出。
     * 为什么呢？
     * 因为在init方法中发生了指令重排，
     * 计算机在处理程序时，发现这个flag最终
     * 会变成false，并且线程的run方法中没有
     * 执行任何的方法，所以直接把run方法中的
     * 值改成false了。
     */
    private static class Question1 {

        static Thread thread;
        static boolean flag = true;


        public static void main(String[] args) {
            // 开启一个线程
            init();
            flag = false;
        }

        public static void init() {
            thread = new Thread() {
                @Override
                public void run() {
                    while (flag) {
                    }
                }
            };
            thread.start();
        }

        /**
         * 程序也会正常退出，虽然线程执行的run方法中
         * 内部执行了test方法，但是test方法内部没有
         * 做任何事情，所以也会发生指令重排，直接把flag
         * 变成false
         */
        private static class Question2 {

            static Thread thread;
            static boolean flag = true;


            public static void main(String[] args) {
                // 开启一个线程
                init();
                flag = false;
            }

            public static void init() {
                thread = new Thread() {
                    @Override
                    public void run() {
                        while (flag) {
                            test();
                        }
                    }
                };
                thread.start();
            }

            public static void test() {

            }
        }

        /**
         * 程序会正常退出，
         * 基于Question2，现在test中有执行的逻辑体了，
         * 所以不会进行指令重排。
         *
         * 同时，主线程和子线程被cpu调度的比例为8:2 = 4:1
         * 所以在执行完init方法后，主线程会立马把flag改成
         * false，线程可能压根不会执行循环体里面的内容
         * 我们可以在run方法的内部打一个断点。
         * 它永远不会进去。
         *
         * 就是因为主线程的调取权比子线程高，
         * 导致子线程在调用native start0方法
         * 启动线程时，有可能主线程早就执行完后面的
         * 代码了，包括设置flag为false。
         * 所以运行的过程中，基本上是程序会正常退出
         */
        private static class Question3 {

            static Thread thread;
            static boolean flag = true;


            public static void main(String[] args) {
                // 开启一个线程
                init();
                flag = false;
            }

            public static void init() {
                thread = new Thread() {
                    @Override
                    public void run() {
                        while (flag) {
                            test();
                        }
                    }
                };
                thread.start();
            }

            public static void test() {
                String x = "hello";
                x.toString();
            }
        }

        /**
         * 基于Question3的问题，
         * 我们把主线程在执行flag = false
         * 的前面睡眠2秒钟.
         *
         * 此时尽管子线程会调用native方法，
         * 但肯定比2秒钟短，
         * 于是线程启动完毕了。
         * 此时程序可能会正常退出可能不会，因为要等到
         * 子线程访问到flag变量才行。
         *
         * 也许它永远不会访问到，则不会停
         * 也许它能访问到，则会停
         */
        private static class Question4 {

            static Thread thread;
            static boolean flag = true;


            public static void main(String[] args) throws InterruptedException {
                // 开启一个线程
                init();
                Thread.sleep(2000);
                flag = false;
            }

            public static void init() {
                thread = new Thread() {
                    @Override
                    public void run() {
                        System.out.println(flag);
                        while (flag) {
                            test();
                        }
                    }
                };
                thread.start();
            }

            public static void test() {
                String x = "hello";
                x.toString();
            }
        }

        /**
         * 基于Question4，
         * 我们只需要在flag中添加一个volatile关键字
         * 防止发生指令重排以及增加可见性即可
         * 此时的程序一定能正常退出。
         * 可能会要一段时间，为什么呢？
         * 还是那句话，要等到线程感知到flag发生变化了
         * 才通知自己线程内部的flag属性
         */
        private static class Question5 {

            static Thread thread;
            static volatile boolean flag = true;


            public static void main(String[] args) throws InterruptedException {
                // 开启一个线程
                init();
                Thread.sleep(2000);
                flag = false;
            }

            public static void init() {
                thread = new Thread() {
                    @Override
                    public void run() {
                        System.out.println(flag);
                        while (flag) {
                            test();
                        }
                    }
                };
                thread.start();
            }

            public static void test() {
                String x = "hello";
                x.toString();
            }
        }

    }

}
