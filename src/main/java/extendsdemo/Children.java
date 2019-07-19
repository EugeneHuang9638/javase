package extendsdemo;

public class Children extends Parent {

    String code = "children";

    public void join() {
        System.out.println(this.code);
        this.test();
    }
}
