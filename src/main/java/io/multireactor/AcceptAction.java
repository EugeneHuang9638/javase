package io.multireactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptAction implements Runnable {

    private ServerSocketChannel ssc;

    private Selector selector;

    public AcceptAction(ServerSocketChannel ssc, Selector selector) {
        this.ssc = ssc;
        this.selector = selector;
    }

    @Override
    public void run() {
        System.out.println("处理连接事件");
        try {
            SocketChannel accept = ssc.accept();
            System.out.println("客户端：" + accept.getRemoteAddress() + " 上线了");

            // 要注册到selector中去，必须要配置非阻塞
            accept.configureBlocking(false);
            // 将连接到服务器的客户端的channel注册到selector中去
            SelectionKey registerKey = accept.register(selector, SelectionKey.OP_READ);
            registerKey.attach(new ReadAndWriteAction(accept, registerKey));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
