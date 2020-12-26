package io.masterslavereactor;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 处理读事件，当有读事件发生时，将业务逻辑的处理分发到logicHandler中执行
 */
public class SlaveReactor implements Runnable {

    private Selector selector;

    private int num;

    public SlaveReactor(int num) throws IOException {
        this.selector = Selector.open();
        this.num = num;
        System.out.println(selector);
    }

    public SelectionKey register(SocketChannel accept) throws IOException {
        // 要注册到selector中去，必须要配置非阻塞
        accept.configureBlocking(false);

        selector.wakeup();
        SelectionKey selectionKey = accept.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(new LogicHandler(accept, selectionKey));

        return selectionKey;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                // 当有读事件发生时，这里为什么不阻塞了？
                if (selector.select() == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("第 " + num + " 个selector得到执行。。");

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

}
