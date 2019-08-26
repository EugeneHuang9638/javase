package program;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 启动三个线程循环输出
 *   eg: 线程一输出 45, 44, 43, 42, 41
 *       线程二输出 40, 39, 38, 37, 36
 *       线程三输出 35, 34, 33, 32, 31
 *
 *       再轮到线程一输出 直到输出0 停止
 *
 *
 * 方法1: 使用重入锁来解决  =>  不可控, 因为调用unlock的时候不确定抢占cpu执行权的线程到底是哪个!
 *        当只是让两个线程互相协作的话，可以完成按照一定规律打印, 若有多个线程, 当重入锁调用unlock方法后,
 *        其他在lock方法阻塞的线程不知道会执行哪个一个! 这都是随机的。
 * 方法2: 使用线程池和信号量来控制(信号量设置为1, 每次运行一个, 线程池中放入三个线程)
 * 方法3: 使用重入锁的Condition特性完美控制线程运行顺序
 */
public class ThreadOneFTF {

    private static Lock lock = new ReentrantLock();

    private static int resource = 45;

    private static int controller = 1;

    static class Method1 {

        static class Thread1 implements Runnable {
            @Override
            public void run() {
                while (resource > 0) {
                    lock.lock();
                    while(controller == 1 && resource > 0) {
                        System.out.println("线程1: " + resource);
                        if (resource-- % 5 == 1) {
                            break;
                        }
                    }
                    controller = 2;
                    lock.unlock();
                }
            }
        }

        static class Thread2 implements Runnable {
            @Override
            public void run() {
                while (resource > 0) {
                    lock.lock();
                    while(controller == 2 && resource > 0) {
                        System.out.println("线程2: " + resource);
                        if (resource-- % 5 == 1) {
                            break;
                        }
                    }
                    controller = 1;
                    lock.unlock();
                }
            }
        }

        static class Thread3 implements Runnable {
            @Override
            public void run() {
                while (resource > 0) {
                    lock.lock();
                    while(controller == 3 && resource > 0) {
                        System.out.println("线程3: " + resource);
                        if (resource-- % 5 == 1) {
                            break;
                        }
                    }
                    controller = 1;
                    lock.unlock();
                }
            }
        }

        public static void main(String[] args) {
            // 方法1:
            new Thread(new Thread1()).start();
            new Thread(new Thread2()).start();
            new Thread(new Thread3()).start();
        }
    }

    static class Method2 {
        private static Semaphore semaphore = new Semaphore(1);

        static class Thread11 implements Runnable {
            @Override
            public void run() {
                while (resource > 0) {
                    try {
                        semaphore.acquire();
                        while(controller == 1 && resource > 0) {
                            System.out.println("线程1: " + resource);
                            if (resource-- % 5 == 1) {
                                break;
                            }
                        }
                        controller = 2;
                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        static class Thread22 implements Runnable {
            @Override
            public void run() {
                while (resource > 0) {
                    try {
                        semaphore.acquire();
                        while(controller == 2 && resource > 0) {
                            System.out.println("线程2: " + resource);
                            if (resource-- % 5 == 1) {
                                break;
                            }
                        }
                        controller = 3;
                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        static class Thread33 implements Runnable {
            @Override
            public void run() {
                while (resource > 0) {
                    try {
                        semaphore.acquire();
                        while(controller == 3 && resource > 0) {
                            System.out.println("线程3: " + resource);
                            if (resource-- % 5 == 1) {
                                break;
                            }
                        }
                        controller = 1;
                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public static void main(String[] args) {


            // 方法2:
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            executorService.submit(new Thread(new Thread11()));
            executorService.submit(new Thread(new Thread22()));
            executorService.submit(new Thread(new Thread33()));
        }
    }

    static class Method3 {

        private static Condition condition1 = lock.newCondition();
        private static Condition condition2 = lock.newCondition();
        private static Condition condition3 = lock.newCondition();


        static class Thread111 implements Runnable {
            @Override
            public void run() {
                while (resource > 0) {
                    try {
                        // 线程1 若无拿到lock的锁, 就会在此阻塞
                        lock.lock();

                        if (controller != 1) {
                            condition1.await();
                        }

                        while(resource > 0) {
                            System.out.println("线程1: " + resource);
                            if (resource-- % 5 == 1) {
                                break;
                            }
                        }
                        controller = 2;
                        condition2.signal();
                        lock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        static class Thread222 implements Runnable {
            @Override
            public void run() {
                while (resource > 0) {
                    try {
                        lock.lock();

                        if (controller != 2) {
                            condition2.await();
                        }

                        while(resource > 0) {
                            System.out.println("线程2: " + resource);
                            if (resource-- % 5 == 1) {
                                break;
                            }
                        }
                        controller = 3;
                        condition3.signal();
                        lock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        static class Thread333 implements Runnable {
            @Override
            public void run() {
                while (resource > 0) {
                    try {
                        lock.lock();

                        if (controller != 3) {
                            condition3.await();
                        }

                        while(resource > 0) {
                            System.out.println("线程3: " + resource);
                            if (resource-- % 5 == 1) {
                                break;
                            }
                        }
                        controller = 1;
                        condition1.signal();
                        lock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public static void main(String[] args) {
            // 方法3:
            new Thread(new Thread111()).start();
            new Thread(new Thread222()).start();
            new Thread(new Thread333()).start();
        }
    }


}
