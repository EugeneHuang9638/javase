package io.nio.masterslavereactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MasterSlaveReactorClient {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        // 一定要配置了非阻塞再连接服务端
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8899));

        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        while (!Thread.interrupted()) {
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            System.out.println(selectionKeys.size());
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isConnectable()) {
                    SocketChannel innerSocketChannel = (SocketChannel) selectionKey.channel();
                    if (innerSocketChannel.isConnectionPending()) {
                        innerSocketChannel.finishConnect();
                    }

                    innerSocketChannel.write(ByteBuffer.wrap("hello server！".getBytes()));
                    innerSocketChannel.configureBlocking(false);
                    // 注册读事件，为了后续接收服务器向客户端发送的数据
                    innerSocketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                    SocketChannel innerSocketChannel = (SocketChannel) selectionKey.channel();
                    StringBuilder sb = new StringBuilder();
                    while (innerSocketChannel.read(byteBuffer) > 0) {
                        // 切换成读模式，读取byteBuffer的数据
                        byteBuffer.flip();
                        sb.append(new String(byteBuffer.array(), 0, (byteBuffer.limit() - byteBuffer.position())));
                    }
                    System.out.println("服务端发来的数据：" + sb);
                }

                iterator.remove();
            }
        }

    }

}
