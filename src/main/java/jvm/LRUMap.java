package jvm;


import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义了一个lruMap，其中内部存储的软引用对象。
 * 它是一个linkedHashMap类型。
 * 与hashMap不同的是，它可以按照插入顺序或访问顺序来维护元素的位置. 并且存储数据就是一个数组，数组内部没有链表。
 *
 *
 */
public class LRUMap<T> extends LinkedHashMap<String, SoftReference<T>> {

    private final int maxSize;

    // 操作lru map的缩，因为linkedHashMap底层用的是hashMap，在put时有并发问题
    private final Lock lruLock = new ReentrantLock();


    /**
     * 设置linkedHashMap可以存在的值
     * @param size
     */
    public LRUMap(int size) {
        // 默认达到75%的空间就会扩容
        super(size, 0.75f, true);
        maxSize = size;
    }

    /**
     * 重写removeEldestEntry方法， 让让LinkHashMap支持LRU
     * 当执行put方法时，会校验下是否要移除当前segment下的链表中最久未被使用的数据
     *
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, SoftReference<T>> eldest) {
        boolean tmp = (size() >= maxSize);
        return tmp;
    }

    public T put(String key, T value) {
        try {

            SoftReference<T> softReference = new SoftReference<T>(value);
            // linkedHashMap底层用的是hashMap，有线程安全问题，这里需要保证put的线程安全
            lruLock.lock();
            // 调用父类方法，把数据放到linkedHashMap中
            super.put(key, softReference);
        }
        finally {
            lruLock.unlock();
        }
        return value;
    }

    /**
     * 获取缓存
     * @param key
     * @return
     */
    public T get(String key) {
        // todo: 看是否需要针对读也加锁，理论上也会有线程安全的问题
        SoftReference<T> cacheValue = super.get(key);
        T refValue = cacheValue.get();
        if (refValue != null) {
            return refValue;
        } else {
            // 软引用内部的引用的对象为空，表示被垃圾回收器清理了，此时我们需要移除这个key对应的SoftReference，减少缓存大小
            this.remove(key);
            return null;
        }
    }

    @Override
    public SoftReference<T> remove(Object key) {
        try {
            lruLock.lock();
            return super.remove(key);
        } finally {
            lruLock.unlock();
        }
    }

    @Override
    public void clear() {
        super.clear();
    }

    public static void main(String[] args) {
//        LRUMap<Integer> lruMap = new LRUMap<>(100);
//        for (int i = 0; i < 99; i++) {
//            lruMap.put(i + "", i);
//        }
//
//        // 模拟访问一下key为3的缓存，此时会把它放在链表的最末尾
//        lruMap.get("3");
//        // 当执行此行代码时，会把key为1的元素remove掉（因为设置的内存最大值为5）
//        lruMap.put("200", 200);
//        System.out.println(1);

        Map<String, Integer> hashMap = new HashMap(100);
        for (int i = 0; i < 2000; i++) {
            hashMap.put(i + "", i);
        }

        System.out.println(1);
    }

}
