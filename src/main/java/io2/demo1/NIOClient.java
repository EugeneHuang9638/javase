package io2.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * NIO客户端
 */
public class NIOClient {

    private Selector selector;


    public static void main(String[] args) throws IOException {
        new NIOClient().startClient();
    }

    private void startClient() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));

        selector = Selector.open();
        // 客户端对连接服务端事件感兴趣
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        Scanner scanner = null;
        while (!Thread.interrupted()) {
            // 客户端中的select方法不会阻塞
            selector.select();


            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                handle(next, "123");
                iterator.remove();
            }

        }

    }

    private void handle(SelectionKey next, String content) throws IOException {
        if (next.isConnectable()) {
            System.out.println("客户端连接成功");
            // 客户端连接服务端成功
            SocketChannel channel = (SocketChannel) next.channel();
            if (channel.isConnectionPending()) {
                channel.finishConnect();
            }

            channel.configureBlocking(false);
            ByteBuffer wrap = ByteBuffer.wrap(content.getBytes());
            channel.write(wrap);
            // 监听服务端传来的数据
            channel.register(selector, SelectionKey.OP_READ);
        } if (next.isReadable()) {
            SocketChannel channel = (SocketChannel) next.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            channel.read(byteBuffer);

            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.limit() - byteBuffer.position()];
            byteBuffer.get(bytes);
            System.out.println("服务端发来的数据：" + new String(bytes));
            // 更新客户端感兴趣的事件
            next.interestOps(SelectionKey.OP_READ);
        } else if (next.isWritable()) {
            System.out.println("客户端发生了写事件");
        }
    }

}
