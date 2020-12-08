package io.onereactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class TCPReactor implements Runnable {

    private ServerSocketChannel ssc;

    private Selector selector;

    public TCPReactor() {
        try {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress("127.0.0.1", 7788));

            selector = Selector.open();

            ssc.configureBlocking(false);

            /**
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

    @Override
    public void run() {

    }
}
