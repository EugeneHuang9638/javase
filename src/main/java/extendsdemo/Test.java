package extendsdemo;

public class Test {

    public static void main(String[] args) {
        Person person = new Children();

        // 此时的person是一个子类的具体对象，调用run方法时当然只调用自己的，父类的方法不显示使用super则不会调用
        person.run();

        // 右移 除以2的n次方
        System.out.println(100 >> 3);

        // 左移 乘以 2的n次方
        System.out.println(2 << 3);

        System.out.println(1 & 7);

        // 绝对值四舍五入 再加符号 Math.round
        System.out.println(Math.round(11.1f));  // 11.0
        System.out.println(Math.round(-11.6f));  // -12.0

        // 有小数部分就向下取整   Math.floor
        System.out.println(Math.floor(-11.6f));  // -12.0
        System.out.println(Math.floor(-11.1f));  // -12.0
        System.out.println(Math.floor(11.1f));  // 11.0
        System.out.println(Math.floor(11.6f));  // 11.0

        // 有小数部分就向上取整  Math.ceil
        System.out.println(Math.ceil(1.1)); // 2.0
        System.out.println(Math.ceil(1.6)); // 2.0
        System.out.println(Math.ceil(0.1)); // 1.0
        System.out.println(Math.ceil(-1.0)); // -1.0
        System.out.println(Math.ceil(-1.6)); // -1.0
    }
}


class Test2 {

    public static void main(String[] args) {
        System.out.println(111);
    }
}