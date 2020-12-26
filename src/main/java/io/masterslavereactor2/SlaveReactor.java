package io.masterslavereactor2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SlaveReactor extends Thread {

    private Selector selector;

    public SlaveReactor() throws IOException {
        this.selector = Selector.open();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                selector.select();
                System.out.println("slaveReactor解除阻塞了");

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    dispatch(next);
                    iterator.remove();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey next) throws IOException {
        SocketChannel channel = (SocketChannel) next.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer);

        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.limit() - byteBuffer.position()];
        byteBuffer.get(bytes);
        System.out.println("接收到客户端的信息：" + new String(bytes));
    }

    /**
     * 注册并处理连接到服务端的socket
     */
    public void handle(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        selector.wakeup();
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

}
