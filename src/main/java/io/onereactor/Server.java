package io.onereactor;

public class Server {

    public static void main(String[] args) {
        TCPReactor reactor = new TCPReactor("127.0.0.1", 7788);
        // 简单的执行run方法
        reactor.run();
    }
}
