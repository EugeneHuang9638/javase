package io.masterslavereactorproblem;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class SlaveReactor implements Runnable {

    private Selector selector;

    public SlaveReactor() throws IOException {
        this.selector = Selector.open();
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
                System.out.println(Thread.currentThread().getName() + " 读事件：" + selectionKey);
                dispatch(selectionKey.attachment());
                iterator.remove();
            }

        }
    }

    /**
     * 这里分发，最终会执行到 主线程处理连接事件时指定的attach
     *
     */
    private void dispatch(Object object) {
        Runnable attachment = (Runnable) object;
        attachment.run();
    }

    public SelectionKey register(SocketChannel accept, int ops) throws IOException {
        // 要注册到selector中去，必须要配置非阻塞
        accept.configureBlocking(false);

        selector.wakeup();
        SelectionKey selectionKey = accept.register(selector, ops);
        selectionKey.attach(new LogicHandler(accept, selectionKey));

        return selectionKey;
    }
}
