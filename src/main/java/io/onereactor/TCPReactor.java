package io.onereactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPReactor implements Runnable {

    private ServerSocketChannel ssc;

    private Selector selector;

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                System.out.println("等待客户端连接.....");
                /**
                 * 根据我们之前的知识点：selector的select方法是会阻塞的，
                 * 也就是说，如果没有感兴趣的事件发生，那么它是不会往下执行的.
                 *
                 * 比如上述的serverSocketChannel注册到selector中去了，并且注册了连接事件，
                 * 当有客户端连接时，此时就算有感兴趣的事件（连接时间）发生了，于是select()方法的阻塞就会
                 * 解除，会继续往下执行。
                 *
                 */
                if (selector.select() == 0) {
                    continue;
                }

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    // 遍历已经发生的事件，进行转发
                    dispatch(selectionKey.attachment());
                    iterator.remove();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public TCPReactor(String host, int port) {
        try {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(host, port));

            selector = Selector.open();

            ssc.configureBlocking(false);

            /**
             * 将serverSocketChannel注册到selector中去
             * 为selectionKey中添加一个附加对象，
             * 如下两行代码跟下面一行是一样的，都是将一个附加对象添加到SelectionKey中
             * ssc.register(selector, SelectionKey.OP_ACCEPT, new AcceptAction(ssc, selector));
             */
            SelectionKey selectionKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
            selectionKey.attach(new AcceptAction(ssc, selector));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(Object attachment) {
        Runnable runnable = (Runnable) attachment;
        runnable.run();
    }
}
