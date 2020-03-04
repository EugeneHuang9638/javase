package extendsdemo;

public class Parent implements Person {

    String code = "parent";

    static int test1 = 1;

    static {
        System.out.println("父类静态代码块");
    }

    {
        System.out.println("StringBuffer父类代码块");
    }

    protected void test() {
        System.out.println("父类test方法: " + this.code);
    }

    public Parent() {
        System.out.println("父类构造方法");
    }

    @Override
    public void run() {
        System.out.println("父类 run方法");
    }

}
