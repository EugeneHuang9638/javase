package io.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * BIO客户端
 */
public class MyNIOClient1 {

    /**
     * 创建一个任务长度为10的线程池，同时工作的线程数为5, 最大线程数为10
     * 空闲时间为60s，拒绝策略为抛异常
     */
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            5,
            10,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 7788));
        ByteBuffer bf = ByteBuffer.allocate(1024);
        Scanner scanner = null;

        // 开启一个线程来处理服务端响应的内容：
        startReadThread(socketChannel);

        while (true) {

            scanner = new Scanner(System.in);
            // 设置scanner遇到换行符才读取数据，默认是遇到空格和换行都会读取数据的
            scanner.useDelimiter("\n");
            String next = scanner.next();

            if ("exit".equals(next)) {
                break;
            }
            bf.put(next.getBytes());
            bf.flip();
            socketChannel.write(bf);
            bf.clear();

        }

        scanner.close();
        socketChannel.close();

    }

    private static void startReadThread(SocketChannel socketChannel) {
        ByteBuffer bf = ByteBuffer.allocate(1024);
        threadPoolExecutor.execute(() -> {
            try {
                while (socketChannel.isConnected()) {
                    /**
                     * 每次读取数据前，先要把缓冲区切换成写模式
                     * 因为在读取数据时，会将数据写入到缓冲区去。
                     * 然后要从缓冲区读数据时，要切换成写模式。
                     *
                     * 因此，针对缓冲区而言，如果要往里面写数据，要先clear一次（改成写模式）
                     * 要往里面读数据，需要执行flip方法，切换成读模式
                     */
                    bf.clear();
                    while (socketChannel.read(bf) > 0) {
                        bf.flip();
                        System.out.println(bf);
                        byte[] bytes = new byte[bf.limit()];
                        bf.get(bytes);
                        System.out.println(new String(bytes));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
