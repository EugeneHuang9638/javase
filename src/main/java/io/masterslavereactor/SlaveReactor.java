package io.masterslavereactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 处理读事件，当有读事件发生时，将业务逻辑的处理分发到logicHandler中执行
 */
public class SlaveReactor implements Runnable {

    /**
     * 每一个slaveReactor中内部都维护了一个多路复用器,
     * 其中注册到此selector中的channel都是客户端的channel，并且只对读事件感兴趣，
     * 因此，只要客户端向服务端写了数据，最终会将此类的select方法解阻塞，继续往下执行
     */
    private Selector selector;

    private int num;

    public SlaveReactor(int num) throws IOException {
        this.selector = Selector.open();
        this.num = num;
        System.out.println(selector);
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
                dispatch(selectionKey);
                iterator.remove();
            }

        }
    }

    public SelectionKey register(SocketChannel accept) throws IOException {
        // 要注册到selector中去，必须要配置非阻塞
        accept.configureBlocking(false);

        selector.wakeup();
        SelectionKey selectionKey = accept.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(new LogicHandler(accept, selectionKey));
        System.out.println("第" + num + "个slaveReactor有客户端连接上了，注册的selectionKey为：" + selectionKey);

        return selectionKey;
    }

    /**
     * 这里分发，最终会执行到 主线程处理连接事件时指定的attach
     *
     */
    private void dispatch(SelectionKey selectionKey) {
        LogicHandler logicHandler = (LogicHandler) selectionKey.attachment();
        logicHandler.run();
    }

    /**
     * 这里分发，最终会执行到 主线程处理连接事件时指定的attach
     * 当使用上面的dispatch方法时，会不停的死循环读事件，当使用此dispatch方法时，一切正常
     */
/*    private void dispatch(SelectionKey selectionKey) {

        SocketChannel channel = (SocketChannel) selectionKey.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            channel.read(byteBuffer);
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.limit() - byteBuffer.position()];
            byteBuffer.get(bytes);
            System.out.println("接收到客户端的信息：" + new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/



}
