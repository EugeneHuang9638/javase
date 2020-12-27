package io.masterslavereactor3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class SlaveReactor extends Thread {

    private Selector selector;

    public SlaveReactor() throws IOException {
        this.selector = Selector.open();
        System.out.println(this.selector);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                selector.select();
                System.out.println(Thread.currentThread().getName() + "：slaveReactor解除阻塞了");

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

        selector.wakeup();
        SelectionKey register = socketChannel.register(selector, SelectionKey.OP_READ);
        register.attach(new LogicHandler());

    }
}
