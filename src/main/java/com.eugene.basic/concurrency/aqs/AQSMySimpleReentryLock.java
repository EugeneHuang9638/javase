package com.eugene.basic.concurrency.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * AQS: Abstract Queued Synchronized 同步发生器
 *  => 用于构建锁  java util concurrent包的基础
 *
 * 基本思想:
 *   通过内置的FIFO同步队列来完成线程争夺资源的管理工作
 * 主要思想:
 *   利用了CLH队列(由开发的三个人名字的第一个字母组成的)
 * 实现方式:
 *   通过双向链表实现, 让线程请求共享资源时, 若资源没有被加锁(一个使用volatile修饰的int类型变量),
 *   则当前线程标志为抢占资源中, 并且将资源加锁, 其他的线程只能在队列中排队(通过自旋锁减少与cpu内
 *   核线程的交互, 因为与内核线程交互式很耗时的), 当抢占资源的线程执行
 *   完成后释放资源, 由队列中的线程去争夺.
 * 主要功能:
 *   写锁的帮助器(非公共内部帮助器类 -> 私有内部类), 提供获取锁和释放锁的功能
 *
 * 内部状态标识:
 *   1. CANCELLED(1): 中断或取消, 不能再次进入队列
 *   2. SIGNAL(-1): 节点的继任者(后结点)被阻塞
 *   3. CONDITION(-2): 条件阻塞
 *   4. PROPAGATE(-3): 指示下一个acquireShared应该无条件传播
 *
 * acquire方法: 独占模式获取(锁)资源对象, 忽略中断
 *   内部tryAcquire方法: 尝试去获取对象(获取不到再自旋)
 * release方法: 独占模式释放锁
 *
 * acquireShared方法: 共享模式获取(锁)资源对象, 忽略中断
 * releaseShared方法: 共享模式释放锁
 *
 * 对于state变量:
 *   对于加锁过程中: state的值只能是 >= arg(传进来的值) >=的原因是有线程重复加锁(重入锁)的case
 *   对于释放锁的过程: state的值一定是 >= 0, 同样的, 大于0的原因是有重入性的原因
 *
 *
 * AQSMySimpleReentryLock: 是一种简单可重入性的独占模式的锁
 *
 *
 * 非公平锁(性能高一些):
 *   1. 直接插队获取锁
 *   2. 不管是公平锁还是非公平锁, 一朝排队，永远排队。 也就是说非公平锁在acquire时, 若失败, 则进队
 *
 */
public class AQSMySimpleReentryLock implements Lock {

    private Helper helper = new Helper();

    /**
     *  非公共内部帮助器类 -> 私有内部类 -> 实现AQS的功能
     *  里面重写的tryAcquire和tryRelease, 是采用了模板方法设计模式,
     *  在调用acquire或release时会回调到子类重写的tryAcquire或tryRelease方法
     */
    private class Helper extends AbstractQueuedSynchronizer {


        /**
         * 重写获取锁逻辑(独占模式),
         * 此处的arg为程序员创建锁时自定义的一个数字X(加锁和解锁时的arg必须一致),
         * 在加锁和解锁时会对state进行操作, 保证线程对资源用完后state处于0(无锁状态)
         * @param arg
         * @return
         */
        @Override
        protected boolean tryAcquire(int arg) {
            int state = getState();

            if (state == 0) {
                // 此处不应该直接给当前线程加锁, 因为有可能当前线程进入队列的时候, 前面还有很多线程在排队
                // 这样的写法 证明这把锁是非公平的: 因为是上来就直接加锁, 不需要考虑前面是否有线程在排队
                if (compareAndSetState(0, arg)) {
                    // 设置当前线程占有资源(独占模式)
                    setExclusiveOwnerThread(Thread.currentThread());
                    return true;
                }
            } else if (getExclusiveOwnerThread() == Thread.currentThread()) {
                setState(getState() + arg);
                return true;
            }

            return false;
        }

        /**
         * 重写释放锁逻辑(独占模式) -> 有两个含义: 1. state会减少arg  2. 若符合释放锁标准, 则释放锁
         * @param arg
         * @return
         */
        @Override
        protected boolean tryRelease(int arg) {
            int state = getState() - arg;
            // 保证只有加了锁的线程才能操作state
            if (Thread.currentThread() != getExclusiveOwnerThread()){
                throw new IllegalMonitorStateException("不是正在运行的线程, 无法释放锁, 终止当前线程执行。");
            }

            // state为0的状态 表示空闲状态, arg为程序员自己传的值,
            boolean flag = state == 0;
            if (flag) {
                setExclusiveOwnerThread(null);
            }

            setState(state); // 重入性问题: 同一个线程加了两次锁, 也调用了两次unlock方法, 保证能恢复原状

            return flag;
        }

        public Condition newConditionObject() {
            return new ConditionObject();
        }
    }

    @Override
    public void lock() {
        // 以独占模式设置state为1
        helper.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        helper.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return helper.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return helper.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        helper.release(1);
    }

    @Override
    public Condition newCondition() {
        return helper.newConditionObject();
    }
}
