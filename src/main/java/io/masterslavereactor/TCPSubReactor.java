package io.masterslavereactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPSubReactor implements Runnable {

    private Selector selector;

    private ServerSocketChannel ssc;

    private int num;

    public TCPSubReactor(Selector selector, ServerSocketChannel ssc, int num) {
        this.selector = selector;
        this.ssc = ssc;
        this.num = num;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                if (selector.select() == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                dispatch(selectionKey);
                iterator.remove();
            }

        }
    }

    /**
     * 这里分发，最终会执行到 主线程处理连接事件时指定的attach
     *
     */
    private void dispatch(SelectionKey selectionKey) {
        Runnable attachment = (Runnable) selectionKey.attachment();
        attachment.run();
    }
}
