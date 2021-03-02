package io.nio.mulreactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MulReactorServer {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            5,
            10,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(30),
            Executors.defaultThreadFactory(),
            (r, executor) -> System.out.println("自定义拒绝策略")
    );

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("127.0.0.1", 8899));
        // 配置了非阻塞才能往selector注册
        ssc.configureBlocking(false);

        // 专注于客户端连接事件 --> 客户端的连接操作，对于服务端而言就是接收事件
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        // 线程为中断则一直执行
        while (!Thread.interrupted()) {
            System.out.println("等待客户端连接..");
            // 此方法是阻塞的，当selector有事件发生时，才会解除阻塞
            selector.select();

            // 获取注册在selector中的channel的感兴趣事件  --> 只有是感兴趣的事件才会被放到set中
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    System.out.println("有客户端连接了..，获取客户端的socketChannel并注册到selector中，同时绑定客户端的读事件");
                    // 先获取到服务端的channel，然后再获取连接到服务端的客户端channel
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel clientSocketChannel = serverSocketChannel.accept();
                    clientSocketChannel.configureBlocking(false);
                    clientSocketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    System.out.println("有客户端向服务器发送数据了..");
                    SocketChannel clientSocketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                    StringBuilder sb = new StringBuilder();
                    while (clientSocketChannel.read(byteBuffer) > 0) {
                        // 切换成读模式，读取byteBuffer的数据
                        byteBuffer.flip();
                        sb.append(new String(byteBuffer.array(), 0, (byteBuffer.limit() - byteBuffer.position())));
                    }

                    THREAD_POOL_EXECUTOR.submit(() -> {
                        System.out.println(Thread.currentThread().getName() + " 异步处理客户端发来的数据：" + sb);
                    });

                    // 往客户端回写数据
                    ByteBuffer writeBuffer = ByteBuffer.wrap("hello client！".getBytes());
                    clientSocketChannel.write(writeBuffer);
                    selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                } else if (selectionKey.isWritable()) {
                    System.out.println("处理服务端的写事件，无数据可写时，将感兴趣的写事件去除");
                    selectionKey.interestOps(SelectionKey.OP_READ);
                }

                iterator.remove();
            }
        }
    }
}
