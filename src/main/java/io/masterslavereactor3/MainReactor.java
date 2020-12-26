package io.masterslavereactor3;

import io.masterslavereactor2.SlaveReactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class MainReactor {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    public MainReactor() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 7788));

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new Acceptor());
    }

    public void start() throws IOException {
        while (!Thread.interrupted()) {
            System.out.println("等待客户端连接...");
            // 阻塞在这里
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                dispatch(key);
                iterator.remove();
            }
        }
    }

    private void dispatch(SelectionKey key) throws IOException {
        ((Acceptor) key.attachment()).handle(key);
    }

    public static void main(String[] args) throws IOException {
        new MainReactor().start();
    }


}
