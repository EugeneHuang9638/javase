package io.masterslavereactor3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class SlaveReactor extends Thread {

    private Selector selector;

    public SlaveReactor() throws IOException {
        this.selector = Selector.open();
        System.out.println(Thread.currentThread().getName() + "线程的selector: " + this.selector);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                System.out.println(Thread.currentThread().getName() + "线程的selector: " + selector + " 正在等待客户端发送请求");
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    dispatch(next);
                    iterator.remove();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey registerKey) throws IOException {
        ((LogicHandler) registerKey.attachment()).handler(registerKey);
    }


    public void register(SocketChannel socketChannel) throws IOException {
        socketChannel.configureBlocking(false);

        System.out.println(Thread.currentThread().getName() + "线程准备唤醒 selector: " + selector);
        selector.wakeup();
        SelectionKey register = socketChannel.register(selector, SelectionKey.OP_READ);
        register.attach(new LogicHandler());

    }
}
