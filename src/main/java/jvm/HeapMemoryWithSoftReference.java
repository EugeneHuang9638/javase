package jvm;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 频繁的list中添加HeapMemoryWithSoftReference对象，
 * 轻易触发fullgc。
 *
 * 需要注意：fullgc清理的是SoftReference内部引用的HeapMemoryWithSoftReference对象，
 * list的元素还是没有变。即当触发fullgc时，list内部的size没有变，还是SoftReference对象，但内部引用的HeapMemoryWithSoftReference
 * 对象被清除了。
 *
 *
 */
public class HeapMemoryWithSoftReference {

    // 表示DumpMemory对象占用内存至少 1024 * 1024 bit 即1m
    private char[] chars = new char[1024 * 1024];

    public static void main(String[] args) throws InterruptedException {
        // 使用java -D命令设置的系统参数可以用System.getProperty(String key); api来获取
        Properties properties = System.getProperties();
        System.out.println(properties.size());
        properties.forEach((a, b) -> System.out.println(a + " >> " + b));
        for (String str : args) {
            System.out.println(str);
        }


        System.out.println("=====================Starting=====================");
        List<SoftReference<HeapMemoryWithSoftReference>> list = new ArrayList();
        List<HeapMemoryWithSoftReference> list2 = new ArrayList();
        for (int i = 0; i <= Integer.MAX_VALUE; i++) {
            // 循环1000次，每隔100毫秒往list添加一个DumpMemory对象， 最后使用jconsole或jvisualvm来定位main线程，来查看堆内存的变化
            Thread.sleep(5);
            list.add(new SoftReference<>(new HeapMemoryWithSoftReference()));
//            list2.add(new HeapMemoryWithSoftReference());
            System.gc();

        }
    }

}
