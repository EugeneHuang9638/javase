package jvm;

public class One {


    public int sum() {
        int a = 3;
        int b = 10;

        return (a + b) * 5;
    }

    public static void main(String[] args) {
        One one = new One();
        System.out.println(one.sum());
    }
}
