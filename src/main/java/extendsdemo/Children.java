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


    /**
     * 此处没有继承父类的sum方法，为什么呢？
     *
     * 因为private设计的初衷就是为了隐藏，不让其他类所知道。
     *
     * 在这里只是具有相同的方法而已。
     *
     * 但要注意，子类会继承父类的私有属性、方法，但是无法从子类中获取
     *
     * @param x
     * @param y
     * @return
     */
    private int sum(int x, int y) {
        return x + y;
    }


}
