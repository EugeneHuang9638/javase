package extendsdemo;

public class Children extends Parent {

    String code = "children";

    static int test = 1;

    static {
        System.out.println("子类静态代码块");
    }

    {
        System.out.println("子类代码块");
    }


    public void join() {
        System.out.println(this.code);
        this.test();
    }

    protected void test() {
        System.out.println("子类test方法");
    }

    public Children() {
        System.out.println("子类构造方法");
    }

    public void run() {
        System.out.println("子类run方法");
    }

}
