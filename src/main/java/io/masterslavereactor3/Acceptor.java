package io.masterslavereactor3;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor {

    /**
     * 获取cpu核心数
     */
    private final int CORS = Runtime.getRuntime().availableProcessors();

    /**
     * 创建从线程数组
     */
    private SlaveReactor[] slaveReactors = new SlaveReactor[CORS];

    private volatile int selIdx = 0;

    public Acceptor() throws IOException {
        for (int i = 0; i < CORS; i++) {
            slaveReactors[i] = new SlaveReactor();
            slaveReactors[i].start();
        }
    }

    /**
     * 注册并处理连接到服务端的socket
     */
    public synchronized void handle(SelectionKey key) throws IOException {

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        slaveReactors[selIdx].register(socketChannel);

        if (++selIdx == CORS) {
            selIdx = 0;
        }
    }

}
