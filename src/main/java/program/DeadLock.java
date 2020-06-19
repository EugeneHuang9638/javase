package program;

/**
 * 实现一个死锁的程序：
 *
 * 所谓死锁：
 *   就是我持有你的锁，但条件是拿到我自己的锁我再释放。
 *   但是你持有的是我的锁，你要拿到自己的锁再释放我的锁
 */
public class DeadLock {

    private static Object lockA = new Object();
    private static Object lockB = new Object();


    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (lockB) {
                System.out.println("线程A拿到lockB这把锁");
                try {
                    System.out.println("线程A模拟处理2s钟逻辑");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (lockA) {
                    System.out.println("拿到lockA这把锁再往下走");
                }
            }
        }, "线程A").start();


        new Thread(() -> {
            synchronized (lockA) {
                System.out.println("线程B拿到lockA这把锁");
                try {
                    System.out.println("线程B模拟处理2s钟逻辑");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (lockB) {
                    System.out.println("拿到lockB这把锁，再继续往下走");
                }

            }
        }, "线程B").start();
    }
}
