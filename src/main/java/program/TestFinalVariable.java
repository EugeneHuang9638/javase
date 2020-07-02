package program;

/**
 * final修饰的变量定义是没有被初始化，
 * 但是能在构造方法(必须保证初始化对象时能对变量进行赋值，否则会报错)
 * 和非静态代码块初始化
 */
public class TestFinalVariable {


    private final String content;

    {
        this.content = "123";
    }

    public static void main(String[] args) {
        TestFinalVariable a = new TestFinalVariable();
        System.out.println(a.content);
    }


}
