package io.onereactor;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class AcceptAction implements Runnable {

    private ServerSocketChannel ssc;

    private Selector selector;

    public AcceptAction(ServerSocketChannel ssc, Selector selector) {
        this.ssc = ssc;
        this.selector = selector;
    }

    @Override
    public void run() {

    }
}
