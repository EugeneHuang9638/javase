package io.masterslavereactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理连接事件，并将业务逻辑分发给acceptAction中去处理
 */
public class MainReactor implements Runnable {

    private ServerSocketChannel ssc;

    private Selector selector;

    public MainReactor(String host, int port) {
        try {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(host, port));

            selector = Selector.open();

            ssc.configureBlocking(false);

            /**
             * 此时只是将服务端的socketChannel传到了selectionKey上
             */
            SelectionKey selectionKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
            selectionKey.attach(new AcceptAction(selectionKey));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                System.out.println("等待客户端连接.....");
                /**
                 * 根据我们之前的知识点：selector的select方法是会阻塞的，
                 * 也就是说，如果没有感兴趣的事件发生，那么它是不会往下执行的（一直阻塞）.
                 *
                 * 比如上述的serverSocketChannel注册到selector中去了，并且注册了连接事件，
                 * 当有客户端连接时，此时就算有感兴趣的事件（连接时间）发生了，于是select()方法的阻塞就会
                 * 解除，会继续往下执行。
                 */
                if (selector.select() == 0) {
                    continue;
                }

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    dispatch(selectionKey.attachment());
                    iterator.remove();
                    // 遍历已经发生的事件，进行转发
                }
                System.out.println("连接事件处理完毕，等待下一个连接");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(Object attachment) {
        Runnable runnable = (Runnable) attachment;
        runnable.run();
    }
}
