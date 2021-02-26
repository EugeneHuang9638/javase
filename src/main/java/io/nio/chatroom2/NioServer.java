package io.nio.chatroom2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress("127.0.0.1", 8899));

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            System.out.println("等待事件发生");
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    System.out.println("有客户端连接，客户端连接事件发生了");
                    ServerSocketChannel inner = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = inner.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    System.out.println("有客户端向服务端发送数据了。");
                    // 拿到客户端的socketChannel
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    StringBuilder sb = new StringBuilder();
                    while (socketChannel.read(buffer) > 0) {
                        buffer.flip();
                        sb.append(new String(buffer.array(), 0, (buffer.limit() - buffer.position())));
                        buffer.clear();
                    }
                    System.out.println("客户端发来的信息：" + sb);

                    // 往客户端发消息
                    ByteBuffer bufferToWrite = ByteBuffer.wrap("HelloClient".getBytes());
                    socketChannel.write(bufferToWrite);
                    key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    System.out.println("write事件");
                    // NIO事件触发是水平触发
                    // 使用Java的NIO编程的时候，在没有数据可以往外写的时候要取消写事件，
                    // 在有数据往外写的时候再注册写事件
                    key.interestOps(SelectionKey.OP_READ);
                }

                iterator.remove();
            }
        }
    }
}
