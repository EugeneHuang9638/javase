package jvm;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HeapMemory {

    // 表示DumpMemory对象占用内存至少 1024 * 100 kb
    private char[] chars = new char[1024 * 100];

    public static void main(String[] args) throws InterruptedException {
        // 使用java -D命令设置的系统参数可以用System.getProperty(String key); api来获取
        Properties properties = System.getProperties();
        System.out.println(properties.size());
        properties.forEach((a, b) -> System.out.println(a + " >> " + b));
        for (String str : args) {
            System.out.println(str);
        }


        System.out.println("=====================Starting=====================");
        List<HeapMemory> list = new ArrayList();
        for (;;) {
            // 循环1000次，每隔100毫秒往list添加一个DumpMemory对象， 最后使用jconsole或jvisualvm来定位main线程，来查看堆内存的变化
            Thread.sleep(100);
            list.add(new HeapMemory());
        }
        // 手动触发fullgc
//        System.gc();
//        System.out.println("end");
    }

}
