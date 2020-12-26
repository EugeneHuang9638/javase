package io.masterslavereactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptAction implements Runnable {

    private final Object LOCK = new Object();

    private SelectionKey selectionKey;

    /**
     * 获取cpu核心数
     */
    private final int CORS = Runtime.getRuntime().availableProcessors();

    /**
     * 创建从线程数组
     */
    private SlaveReactor[] slaveReactors = new SlaveReactor[CORS];

    private volatile int selIdx = 0;

    public AcceptAction(SelectionKey selectionKey) throws IOException {
        this.selectionKey = selectionKey;

        /**
         * 当服务器启动后，会启动多个线程（根据cpu核心数决定）
         * 同时每个线程执行的业务逻辑为TCPSubReactor类的run方法
         *
         * 在初始化的过程中，内部打开了selector
         */
        for (int i = 0; i < CORS; i++) {
            slaveReactors[i] = new SlaveReactor(i);
            new Thread(slaveReactors[i]).start();
        }
    }

    /**
     * 添加锁，保证selIdx的增加不会受到影响
     * 每个客户端连接时，轮询注册到selector中去
     */
    @Override
    public void run() {

        synchronized (LOCK) {
            System.out.println("处理连接事件");
            try {
                ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel accept = channel.accept();
                System.out.println("客户端：" + accept.getRemoteAddress() + " 上线了");

                /**
                 * 将连接到服务器的客户端的channel注册到selectors数组中的第一个，以此类推。
                 * 后续处理对应selector中的事件由对应负责selector的线程负责
                 */
                slaveReactors[selIdx].register(accept);

                if (++selIdx == CORS) {
                    selIdx = 0;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
