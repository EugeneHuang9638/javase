package jvm;

import java.util.ArrayList;
import java.util.List;

public class HeapMemory {

    // 表示DumpMemory对象占用内存至少 1024 * 100 kb
    private char[] chars = new char[1024 * 100];

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting");
        List<HeapMemory> list = new ArrayList();
        for (int i = 0; i < 1000; i++) {
            // 循环1000次，每隔100毫秒往list添加一个DumpMemory对象， 最后使用jconsole来定位main线程，来查看堆内存的变化
            Thread.sleep(100);
            list.add(new HeapMemory());
        }
        System.out.println("end");
    }

}
