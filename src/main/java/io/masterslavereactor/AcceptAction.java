package io.masterslavereactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptAction implements Runnable {

    private ServerSocketChannel ssc;

    /**
     * 获取cpu核心数
     */
    private final int CORS = Runtime.getRuntime().availableProcessors();

    /**
     * 创建selector数组
     */
    private final Selector[] selectors = new Selector[CORS];

    private int selIdx = 0;

    public AcceptAction(ServerSocketChannel ssc) throws IOException {
        this.ssc = ssc;

        /**
         * 当服务器启动后，会启动多个线程（根据cpu核心数决定）
         * 同时每个线程执行的业务逻辑为TCPSubReactor类的run方法
         *
         * 在初始化的过程中，内部打开了selector
         */
        for (int i = 0; i < CORS; i++) {
            selectors[i] = Selector.open();
            new Thread(new TCPSubReactor(selectors[i], ssc ,i)).start();
        }
    }

    @Override
    public void run() {
        System.out.println("处理连接事件");
        try {
            SocketChannel accept = ssc.accept();
            System.out.println("客户端：" + accept.getRemoteAddress() + " 上线了");

            // 要注册到selector中去，必须要配置非阻塞
            accept.configureBlocking(false);
            /**
             * 将连接到服务器的客户端的channel注册到selectors数组中的第一个，以此类推。
             * 后续处理对应selector中的事件由对应负责selector的线程负责
             */
            SelectionKey registerKey = accept.register(selectors[selIdx], SelectionKey.OP_READ);
            registerKey.attach(new ReadAndWriteAction(accept, registerKey));

            // 刷新selector的事件
            selectors[selIdx].wakeup();

            if (++selIdx == selectors.length) {
                selIdx = 0;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
