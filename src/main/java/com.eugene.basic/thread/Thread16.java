package com.eugene.basic.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 测试list并发下会抛出的异常
 *
 * java.util.ConcurrentModificationException
 *
 * 迭代器遍历时 不允许更新操作
 */
public class Thread16 {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i + "");
        }

        Iterator<String> strings = list.iterator();
        while (strings.hasNext()) {
            String value = strings.next();
            if (value.equals("5")) {
                list.remove(value);
            }
        }
    }
}
