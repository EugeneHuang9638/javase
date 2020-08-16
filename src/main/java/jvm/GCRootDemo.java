package jvm;

/**
 * 可以被视为根对象的情况
 * 1、栈的栈帧的局部变量表中引用的对象
 * 2、静态属性
 * 3、常量
 * 4、JNI中的native
 *
 */
public class GCRootDemo {

    // 静态属性可以当做根
    private static GCRootDemo1 gcRootDemo1;

    // 常量也可以当做根
    private static final GCRootDemo2 gcRootDemo2 = new GCRootDemo2();

    public static void m1() {
        // 一个方法在调用的时候，就会产生一个栈帧，内部创建的对象就会存到局部变量表中去，因此demo也是一个根对象
        GCRootDemo demo = new GCRootDemo();

        System.gc();
        System.out.println("第一次GC完成");
    }

    public static void main(String[] args) {
        m1();
    }
}

class GCRootDemo1 {

}

class GCRootDemo2 {

}
