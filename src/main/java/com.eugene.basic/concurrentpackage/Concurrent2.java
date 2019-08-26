package com.eugene.basic.concurrentpackage;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试重入锁搭档: Condition
 *
 * 前有synchronized, wait, notify方法配套使用控制线程的等待与执行
 *
 * 后有ReentrantLock和Condition配套使用控制线程的等待与执行
 *
 * Condition await()和signal()和ReentrantLock配套使用步骤
 *   1. condition是由ReentrantLock对象的实例方法newCondition()创建
 *   2. 使用condition.await()方式阻塞线程时, 要求当前线程拥有
 *      第一步骤所述的ReentrantLock对象的重入锁, 调用后当前线程会释放重入锁
 *   3. 在唤醒上述第二步阻塞的线程时(执行condition.signal()方法), 需要使
 *      用同一个condition对象,并且要求当前线程也要拥有第一条中ReentrantLock对象
 *      的重入锁, 并执行完之后需要将重入锁释放, 这样上述第二步才能重新拿到重入锁并
 *      完成线程后续的逻辑。
 *
 */
public class Concurrent2 {

    public static class ReenterLockCondition implements Runnable {

        public static ReentrantLock lock = new ReentrantLock();
        public static Condition condition = lock.newCondition();

        @Override
        public void run() {

            try {
                lock.lock();
                condition.await();

                // 主线程 47-49行为重新唤醒线程继续执行的代码
                System.out.println("Thread is going on");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public static void main(String[] args) throws InterruptedException {
            ReenterLockCondition tl = new ReenterLockCondition();
            Thread t1 = new Thread(tl);
            t1.start();

            Thread.sleep(2000);

            // 通知线程t1继续执行,
            lock.lock();
            condition.signal();
            lock.unlock();
        }
    }
}
