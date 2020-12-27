package io.masterslavereactorproblem;

public class Server {

    public static void main(String[] args) {
        MainReactor reactor = new MainReactor("127.0.0.1", 7788);
        // 简单的执行run方法
        new Thread(reactor).start();
    }
}
