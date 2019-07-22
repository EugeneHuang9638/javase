package com.eugene.basic.thread;

/**
 * 错误的加锁, 由于jdk底层原理实现误认为synchronized失效
 */
public class Thread15 {

    public static class BadLockOnInteger implements Runnable{
        public static Integer i = 0;

        static BadLockOnInteger instance = new BadLockOnInteger();

        @Override
        public void run() {
            for (int j = 0; j < 10000000; j++) {
                synchronized(i) {
                    // 在jvm执行时, 这是这样的一段代码:  i = Integer.valueOf(i.intValue() + 1),
                    // 跟踪Integer.valueOf()源码可知, 每次都是返回一个新的Integer对象, 导致加锁的都是新对象,当然会导致多线程同步失效
                    i++;
                }
            }

            // javap -c 当前字节码文件
            /*
            Code:
               0: iconst_0                                               ====>     将int类型常量0压入栈
               1: istore_1                                               ====>     将int类型值存入局部变量1 也就是j变量 j = 1
               2: iload_1                                                ====>     加载局部变量1 也就是j
               3: ldc           #2  // int 10000000                      ====>     把常量池中的项(10000000)压入栈
               5: if_icmpge     36                                       ====>     如果一个int类型值大于或者等于另外一个int类型值, 则跳转第36步
               8: getstatic     #3  // Field i:Ljava/lang/Integer;       ====>     从类中获取静态字段
              11: astore_2                                               ====>     将引用类型或returnAddress类型值存入局部变量2  ?????
              12: getstatic     #3  // Field i:Ljava/lang/Integer;       ====>     从类中获取静态字段, 后面接了#3 即获取#3的常量 10000000
              15: invokevirtual #4  // Method java/lang/Integer.intValue:()I   ====>  调度对象的实例方法, 后面说了是第4步, 的intValue方法
              18: iconst_1                                               ====>     iconst_1
              19: iadd                                                   ====>     执行加法运算
              20: invokestatic  #5  // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
              23: dup
              24: putstatic     #3  // Field i:Ljava/lang/Integer;
              27: astore_3
              28: aload_2
              29: pop
              30: iinc          1, 1
              33: goto          2
              36: return
             */
        }


        public static void main(String[] args) throws InterruptedException {
            Thread t1 = new Thread(instance);
            Thread t2 = new Thread(instance);

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            System.out.println(i);
        }
    }


}
